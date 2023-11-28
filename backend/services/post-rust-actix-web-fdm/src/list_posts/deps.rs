use super::*;
use crate::common::api::handle_internal_error;
use crate::common::db::*;
use crate::common::utils::Deps;
use futures_util::{StreamExt, TryStreamExt};
use std::iter::FromIterator;
use std::{collections::HashMap, io::Write as IoWrite};

pub async fn workflow(
    deps: &Deps,
    Query {
        creator,
        page,
        page_size,
    }: Query,
) -> Result<Output> {
    let mut creator_map: Option<HashMap<u64, UserName>> = None;
    let mut where_statement: String = String::new();

    if let Some(creator) = creator {
        let db_creator_id = db::parse_id(&creator.0).ok_or_else(creator_not_found)?;
        let creator_name: (String,) = sqlx::query_as(&format!(
            "SELECT `{USER_USER_NAME}` FROM `{USER}` WHERE `{USER_USER_ID}`=?"
        ))
        .bind(db_creator_id)
        .fetch_optional(&deps.pool)
        .await
        .map_err(handle_internal_error)?
        .ok_or_else(creator_not_found)?;

        let creator_name = UserName(creator_name.0.into());
        where_statement = format!(" WHERE `{POST_CREATOR}` = {}", db_creator_id);
        creator_map = Some(HashMap::from_iter([(db_creator_id, creator_name)]))
    }

    let (total,): (u64,) = sqlx::query_as(&format!(
        "SELECT COUNT(*) FROM `{POST}`{where_statement}"
    ))
        .fetch_one(&deps.pool)
        .await
        .map_err(handle_internal_error)?;

    let offset = (page.0 - 1) * page_size.0;
    let posts_db_result: Vec<(u64, u64, u64, String)> = sqlx::query_as(&format!(
        "SELECT `{POST_POST_ID}`,\
        `{POST_CREATOR}`,\
        `{POST_CREATION_TIME}`,\
        `{POST_TITLE}` \
        FROM `{POST}`{where_statement} \
        ORDER BY `{POST_CREATION_TIME}` DESC, `{POST_POST_ID}` DESC LIMIT ?,?"
    ))
        .bind(offset)
        .bind(page_size.0)
        .fetch_all(&deps.pool)
        .await
        .map_err(handle_internal_error)?;

    if posts_db_result.is_empty() {
        return Ok(Output { total, posts: Vec::new() });
    }

    let creator_map: HashMap<u64, UserName> = if let Some(c) = creator_map {
        c
    } else {
        // Because sqlx doesn't support binding a slice for MySQL.
        // I have to manually make the query.
        let mut users_query: Vec<u8> = format!(
            "SELECT `{USER_USER_ID}`,`{USER_USER_NAME}` FROM `{USER}` WHERE `{USER_USER_ID}` IN ("
        )
        .into();
        for (_, creator, _, _) in posts_db_result.iter() {
            write!(&mut users_query, "{},", *creator).unwrap();
        }
        // replacing the trailing comma cause MySQL doesn't support it.
        *users_query.last_mut().unwrap() = b')';

        sqlx::query_as(&String::from_utf8(users_query).unwrap())
            .fetch(&deps.pool)
            .map(|r| -> Result<(u64, UserName)> {
                let (id, username): (u64, String) = r.map_err(handle_internal_error)?;
                Ok((id, UserName(username.into())))
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
                title: Title(title),
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

    Ok(Output { total, posts })
}
