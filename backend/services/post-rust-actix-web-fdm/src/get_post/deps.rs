use super::*;
use crate::common::api::*;
use crate::common::utils::error::*;

pub async fn workflow(deps: &utils::Deps, input: Query) -> Result<PostInfoForPage> {
    let (creator, creation_time_utc, last_modified_utc, title, text, url): (
        u64,
        u64,
        Option<u64>,
        String,
        Option<String>,
        Option<String>,
    ) = sqlx::query_as(&iformat!(
        "SELECT `" db::posts::CREATOR
        "`,`" db::posts::CREATION_TIME
        "`,`" db::posts::LAST_MODIFIED
        "`,`" db::posts::TITLE
        "`,`" db::posts::TEXT
        "`,`" db::posts::URL
        "` FROM `" db::POSTS "` WHERE `" db::posts::POST_ID "`=?"
    ))
    .bind(input.0)
    .fetch_optional(&deps.pool)
    .await
    .map_err(handle_internal_error)?
    .ok_or_else(post_not_found)?;
    let (creator_name,): (String,) = sqlx::query_as(&iformat!(
        "SELECT `" db::users::USER_NAME "` FROM `" db::USERS "` WHERE `" db::users::USER_ID "`=?"
    ))
    .bind(creator)
    .fetch_optional(&deps.pool)
    .await
    .map_err(handle_internal_error)?
    .ok_or_else(|| {
        handle_internal_error(format_args!(
            "post {} has creator {} which not exists",
            input.0, creator
        ))
    })?;

    Ok(PostInfoForPage {
        creator: CreatorInfo {
            name: UserName::try_new(creator_name).map_err(handle_invalid_value_in_db(
                db::USERS,
                db::users::USER_NAME.into(),
                creator,
            ))?,
            id: UserId(creator),
        },
        creation: Time {
            utc: creation_time_utc,
        },
        last_modified: last_modified_utc.map(|utc| Time { utc }),
        title: Title::try_new(title).map_err(handle_invalid_value_in_db(
            db::POSTS,
            db::posts::TITLE.into(),
            input.0,
        ))?,
        content: match (text, url) {
            (Some(text), None) => PostContent::Text(TextPostContent::try_new(text).map_err(
                handle_invalid_value_in_db(db::POSTS, db::posts::TEXT, input.0),
            )?),
            (None, Some(url)) => PostContent::Url(UrlPostContent::try_new(url).map_err(
                handle_invalid_value_in_db(db::POSTS, db::posts::URL, input.0),
            )?),
            _ => {
                return Err(handle_internal_error(format_args!(
                    "post {} has both or neither post and url",
                    input.0
                )))
            }
        },
    })
}
