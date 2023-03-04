use super::*;
use crate::common::{api::handle_internal_error, db::*};

pub async fn workflow(deps: &crate::common::utils::Deps, input: Query) -> Result<User> {
    let db_id = db::parse_id(&input.0).ok_or_else(user_not_found)?;
    let (user_name, creation_time_utc): (String, u64) = sqlx::query_as(&format!(
        "SELECT `{USER_USER_NAME}`,`{USER_CREATION_TIME}` FROM `{USER}` WHERE `{USER_USER_ID}`=?"
    ))
    .bind(db_id)
    .fetch_optional(&deps.pool)
    .await
    .map_err(handle_internal_error)?
    .ok_or_else(user_not_found)?;
    Ok(User {
        user_name: UserName(user_name.into()),
        creation: Time {
            utc: creation_time_utc,
        },
    })
}
