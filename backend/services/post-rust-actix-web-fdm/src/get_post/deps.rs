use super::*;
use crate::common::{api::*, db::*};

pub async fn workflow(deps: &utils::Deps, input: Query) -> Result<Post> {
    let db_post_id = db::parse_id(&input.0).ok_or_else(post_not_found)?;
    let (creator, creation_time_utc, last_modified_utc, title, text, url): (
        u64,
        u64,
        Option<u64>,
        String,
        Option<String>,
        Option<String>,
    ) = sqlx::query_as(&format!(
        "SELECT `{POST_CREATOR}`,\
        `{POST_CREATION_TIME}`,\
        `{POST_LAST_MODIFIED}`,\
        `{POST_TITLE}`,\
        `{POST_TEXT}`,\
        `{POST_URL}` \
        FROM `{POST}` WHERE `{POST_POST_ID}`=?"
    ))
    .bind(db_post_id)
    .fetch_optional(&deps.pool)
    .await
    .map_err(handle_internal_error)?
    .ok_or_else(post_not_found)?;

    let (creator_name,): (String,) = sqlx::query_as(&format!(
        "SELECT `{USER_USER_NAME}` FROM `{USER}` WHERE `{USER_USER_ID}`=?"
    ))
    .bind(creator)
    .fetch_optional(&deps.pool)
    .await
    .map_err(handle_internal_error)?
    .ok_or_else(|| {
        handle_internal_error(format_args!(
            "post `{}` has creator `{}` which not exists",
            input.0, creator
        ))
    })?;

    Ok(Post {
        creator: Creator {
            name: UserName(creator_name.into()),
            id: UserId(db::format_id(creator)),
        },
        creation: Time {
            utc: creation_time_utc,
        },
        last_modified: last_modified_utc.map(|utc| Time { utc }),
        title: Title(title),
        content: match (text, url) {
            (Some(text), None) => PostContent::Text(TextPostContent(text)),
            (None, Some(url)) => PostContent::Url(UrlPostContent(url)),
            _ => {
                return Err(handle_internal_error(format_args!(
                    "post with ID `{}` has both or neither post and url",
                    db_post_id
                )))
            }
        },
    })
}
