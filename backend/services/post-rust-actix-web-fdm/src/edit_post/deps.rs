use super::*;
use crate::common::{api::*, *};

pub async fn checks_user_is_creator_and_content_has_the_same_post_type(
    deps: &utils::Deps,
    post: &PostId,
    user: &UserId,
    content: &PostContent,
) -> Result<()> {
    let post_db_id = db::parse_id(&post.0).ok_or_else(post_not_found)?;

    let (creator, is_text): (u64, bool) = sqlx::query_as(&iformat!("SELECT `" db::posts::CREATOR "`, (`" db::posts::TEXT "` IS NOT NULL) FROM `" db::POSTS "` WHERE `" db::posts::POST_ID "`=?"))
        .bind(post_db_id)
        .fetch_optional(&deps.pool)
        .await
        .map_err(handle_internal_error)?
        .ok_or_else(post_not_found)?;
    if Some(creator) != db::parse_id(&user.0) {
        return Err(not_creator());
    }

    let content_type_matched = match content {
        PostContent::Text(_) => is_text,
        PostContent::Url(_) => !is_text,
    };

    if !content_type_matched {
        return Err(type_diff());
    }

    Ok(())
}

pub async fn update_post(
    deps: &utils::Deps,
    post_id: &PostId,
    new_content: PostContent,
) -> Result<()> {
    let post_db_id = db::parse_id(&post_id.0).ok_or_else(post_not_found)?;

    let (text, url) = match new_content {
        PostContent::Text(text) => (Some(text.0), None),
        PostContent::Url(url) => (None, Some(url.0)),
    };

    let done = sqlx::query(&iformat!("UPDATE `" db::POSTS "` SET `" db::posts::TEXT "`=?,`" db::posts::URL "`=?,`" db::posts::LAST_MODIFIED "`=? WHERE `" db::posts::POST_ID "`=?"))
        .bind(text)
        .bind(url)
        .bind(Time::now().utc)
        .bind(post_db_id)
        .execute(&deps.pool)
        .await
        .map_err(handle_internal_error)?;

    if done.rows_affected() == 0 {
        Err(post_not_found())
    } else {
        Ok(())
    }
}
