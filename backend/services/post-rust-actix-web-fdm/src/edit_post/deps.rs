use super::*;
use crate::common::{*, api::*};

pub async fn checks_user_is_creator_and_content_has_the_same_post_type(
    deps: &utils::Deps,
    post: PostId,
    user: UserId,
    content: &PostContent,
) -> Result<()> {
    let (creator, is_post): (u64, bool) = sqlx::query_as(&iformat!("SELECT `" db::posts::CREATOR "`, (`" db::posts::POST "` IS NOT NULL) FROM `" db::POSTS "` WHERE `" db::posts::POST_ID "`=?"))
        .bind(post.0)
        .fetch_optional(&deps.pool)
        .await
        .map_err(handle_internal_error)?
        .ok_or_else(post_not_found)?;
    if creator != user.0 {
        return Err(not_creator());
    }
    
    let content_type_matched = match content {
        PostContent::Post(_) => is_post,
        PostContent::Url(_) => !is_post,
    };

    if !content_type_matched {
        return Err(type_diff());
    }

    Ok(())
}

pub async fn update_post(deps: &utils::Deps, post_id: PostId, new_content: PostContent) -> Result<()> {
    let (post, url) = match new_content {
        PostContent::Post(post) => (Some(post), None),
        PostContent::Url(url) => (None, Some(url.to_string())),
    };
    
    let done = sqlx::query(&iformat!("UPDATE `" db::POSTS "` SET `" db::posts::POST "`=?,`" db::posts::URL "`=?,`" db::posts::LAST_MODIFIED "`=? WHERE `" db::posts::POST_ID "`=?"))
        .bind(post)
        .bind(url)
        .bind(Time::now().utc)
        .bind(post_id.0)
        .execute(&deps.pool)
        .await
        .map_err(handle_internal_error)?;

    if done.rows_affected() == 0 {
        Err(post_not_found())
    } else {
        Ok(())
    }
}
