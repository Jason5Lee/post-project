use super::*;
use crate::common::{api::handle_internal_error, *};

pub async fn get_post_creator(deps: &utils::Deps, post: PostId) -> Result<UserId> {
    sqlx::query_as(&iformat!("SELECT `" db::posts::CREATOR "` FROM `" db::POSTS "` WHERE `" db::posts::POST_ID "`=?"))
        .bind(post.0)
        .fetch_optional(&deps.pool)
        .await
        .map_err(handle_internal_error)?
        .ok_or_else(post_not_found)
        .map(|(creator,): (u64,)| UserId(creator))
}

pub async fn delete_post(deps: &utils::Deps, post: PostId) -> Result<()> {
    let rows_affected =
        sqlx::query(&iformat!("DELETE FROM `" db::POSTS "` WHERE `" db::posts::POST_ID "`=?"))
            .bind(post.0)
            .execute(&deps.pool)
            .await
            .map_err(handle_internal_error)?
            .rows_affected();
    if rows_affected != 0 {
        Ok(())
    } else {
        Err(post_not_found())
    }
}
