use super::*;
use crate::common::{api::handle_internal_error, *};

pub async fn get_post_creator(deps: &utils::Deps, post: &PostId) -> Result<UserId> {
    let db_id = db::parse_id(&post.0).ok_or_else(post_not_found)?;
    sqlx::query_as(&iformat!("SELECT `" db::posts::CREATOR "` FROM `" db::POSTS "` WHERE `" db::posts::POST_ID "`=?"))
        .bind(db_id)
        .fetch_optional(&deps.pool)
        .await
        .map_err(handle_internal_error)?
        .ok_or_else(post_not_found)
        .map(|(creator,): (u64,)| UserId(db::format_id(creator)))
}

pub async fn delete_post(deps: &utils::Deps, post: &PostId) -> Result<()> {
    let db_id = db::parse_id(&post.0).ok_or_else(post_not_found)?;

    let rows_affected =
        sqlx::query(&iformat!("DELETE FROM `" db::POSTS "` WHERE `" db::posts::POST_ID "`=?"))
            .bind(db_id)
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
