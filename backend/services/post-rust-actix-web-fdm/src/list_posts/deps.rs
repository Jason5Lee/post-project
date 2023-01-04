use super::*;
use crate::common::api::handle_internal_error;
use crate::common::utils::error::*;
use crate::common::utils::Deps;
use futures_util::{StreamExt, TryStreamExt};
use std::{collections::HashMap, fmt::Write, io::Write as IoWrite};

pub async fn workflow(
    deps: &Deps,
    Query {
        creator,
        condition,
        size,
    }: Query,
) -> Result<Output> {
    // For convenient I don't use binding for time range.
    let (mut post_condition_sql, order) = match condition {
        Condition::No => ("WHERE true".to_string(), "DESC"),
        Condition::Before(before) => (
            iformat!("WHERE `" db::posts::CREATION_TIME "` < " before.utc),
            "DESC",
        ),
        Condition::After(after) => (
            iformat!("WHERE `" db::posts::CREATION_TIME "` > " after.utc),
            "ASC",
        ),
    };
    if let Some(creator) = creator {
        let found_creator: Option<(u64,)> = sqlx::query_as(&iformat!(
            "SELECT `" db::users::USER_ID "` FROM `" db::USERS"` WHERE `" db::users::USER_ID "`=?"
        ))
        .bind(creator.0)
        .fetch_optional(&deps.pool)
        .await
        .map_err(handle_internal_error)?;
        if found_creator.is_none() {
            return Err(creator_not_found());
        }

        iwrite!(&mut post_condition_sql, " AND `" db::posts::CREATOR "` = " creator.0).unwrap();
    }
    let post_sql = iformat!(
        "SELECT `" db::posts::POST_ID
        "`,`" db::posts::CREATOR
        "`,`" db::posts::CREATION_TIME
        "`,`" db::posts::TITLE
        "` FROM `" db::POSTS "` " post_condition_sql
        " ORDER BY `" db::posts::CREATION_TIME "` " order " LIMIT ?"
    );
    let mut posts_db_result: Vec<(u64, u64, u64, String)> = sqlx::query_as(&post_sql)
        .bind(size.to_u32())
        .fetch_all(&deps.pool)
        .await
        .map_err(handle_internal_error)?;

    if posts_db_result.is_empty() {
        return Ok(Output { posts: Vec::new() });
    }

    if order == "ASC" {
        posts_db_result.reverse();
    }
    // Because sqlx doesn't support binding a slice for MySQL.
    // I have to manually make the query.
    let mut users_query: Vec<u8> = iformat!("SELECT `" db::users::USER_ID "`,`" db::users::USER_NAME "` FROM `" db::USERS "` WHERE `" db::users::USER_ID "` IN (").into();
    for (_, creator, _, _) in posts_db_result.iter() {
        write!(&mut users_query, "{},", *creator).unwrap();
    }
    // replacing the trailing comma cause MySQL doesn't support it.
    *users_query.last_mut().unwrap() = b')';

    let user_id_to_name: HashMap<u64, UserName> =
        sqlx::query_as(&String::from_utf8(users_query).unwrap())
            .fetch(&deps.pool)
            .map(|r| -> Result<(u64, UserName)> {
                let (id, username): (u64, String) = r.map_err(handle_internal_error)?;
                Ok((
                    id,
                    UserName::try_new(username).map_err(handle_invalid_value_in_db(
                        db::USERS,
                        db::users::USER_NAME.into(),
                        id,
                    ))?,
                ))
            })
            .try_collect()
            .await
            .map_err(handle_internal_error)?;

    let posts = posts_db_result
        .into_iter()
        .map(|(id, creator, creation_time_utc, title)| {
            Ok(PostInfo {
                id: PostId(id),
                title: Title::try_new(title).map_err(handle_invalid_value_in_db(
                    db::POSTS,
                    db::posts::TITLE.into(),
                    id,
                ))?,
                creator: user_id_to_name
                    .get(&creator)
                    .map(|user_name| CreatorInfo {
                        id: UserId(creator),
                        name: user_name.clone(),
                    })
                    .ok_or_else(|| {
                        let value_err = (
                            creator,
                            ErrorBody {
                                error: ErrBody {
                                    error: "CREATOR_NOT_EXISTS".into(),
                                    reason: "creator of post not found in users".to_string(),
                                    message: "".to_string(),
                                },
                            },
                        );
                        handle_invalid_value_in_db(db::POSTS, db::posts::CREATOR.into(), id)(
                            value_err,
                        )
                    })?,
                creation: Time {
                    utc: creation_time_utc,
                },
            })
        })
        .collect::<Result<Vec<PostInfo>>>()?;

    Ok(Output { posts })
}
