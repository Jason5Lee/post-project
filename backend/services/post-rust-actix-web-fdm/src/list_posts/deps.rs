use super::*;
use crate::common::api::handle_internal_error;
use crate::common::utils::error::*;
use crate::common::utils::Deps;
use futures_util::{StreamExt, TryStreamExt};
use std::iter::FromIterator;
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
    let mut creator_map: Option<HashMap<u64, UserName>> = None;
    if let Some(creator) = creator {
        let db_creator_id = db::parse_id(&creator.0).ok_or_else(creator_not_found)?;
        let creator_name: (String,) = sqlx::query_as(&iformat!(
            "SELECT `" db::users::USER_NAME "` FROM `" db::USERS"` WHERE `" db::users::USER_ID "`=?"
        ))
        .bind(db_creator_id)
        .fetch_optional(&deps.pool)
        .await
        .map_err(handle_internal_error)?
        .ok_or_else(creator_not_found)?;

        let creator_name = UserName::try_new(creator_name.0).map_err(
            handle_invalid_value_in_db(db::USERS, db::users::USER_NAME.into(), db_creator_id),
        )?;
        iwrite!(&mut post_condition_sql, " AND `" db::posts::CREATOR "` = " db_creator_id).unwrap();
        creator_map = Some(HashMap::from_iter([(db_creator_id, creator_name)]))
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

    let creator_map: HashMap<u64, UserName> = if let Some(c) = creator_map {
        c
    } else {
        // Because sqlx doesn't support binding a slice for MySQL.
        // I have to manually make the query.
        let mut users_query: Vec<u8> = iformat!("SELECT `" db::users::USER_ID "`,`" db::users::USER_NAME "` FROM `" db::USERS "` WHERE `" db::users::USER_ID "` IN (").into();
        for (_, creator, _, _) in posts_db_result.iter() {
            write!(&mut users_query, "{},", *creator).unwrap();
        }
        // replacing the trailing comma cause MySQL doesn't support it.
        *users_query.last_mut().unwrap() = b')';

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
            .map_err(handle_internal_error)?
    };

    let posts = posts_db_result
        .into_iter()
        .map(|(id, creator, creation_time_utc, title)| {
            Ok(Post {
                id: PostId(db::format_id(id)),
                title: Title::try_new(title).map_err(handle_invalid_value_in_db(
                    db::POSTS,
                    db::posts::TITLE.into(),
                    id,
                ))?,
                creator: creator_map
                    .get(&creator)
                    .map(|user_name| Creator {
                        id: UserId(db::format_id(creator)),
                        name: user_name.clone(),
                    })
                    .ok_or_else(|| {
                        handle_internal_error(format_args!(
                            "post `{}` has creator `{}` which not exists",
                            id, creator
                        ))
                    })?,
                creation: Time {
                    utc: creation_time_utc,
                },
            })
        })
        .collect::<Result<Vec<Post>>>()?;

    Ok(Output { posts })
}
