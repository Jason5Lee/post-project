use super::*;
use crate::common::api::handle_internal_error;
use crate::common::utils::error::handle_invalid_value_in_db;

pub async fn workflow(deps: &crate::common::utils::Deps, input: Query) -> Result<User> {
    let db_id = db::parse_id(&input.0).ok_or_else(user_not_found)?;
    let (user_name, creation_time_utc): (String, u64) = sqlx::query_as(&iformat!("SELECT `" db::users::USER_NAME "`,`" db::users::CREATION_TIME "` FROM `" db::USERS "` WHERE `" db::users::USER_ID "`=?"))
        .bind(db_id)
        .fetch_optional(&deps.pool)
        .await
        .map_err(handle_internal_error)?
        .ok_or_else(user_not_found)?;
    Ok(User {
        user_name: UserName::try_new(user_name).map_err(handle_invalid_value_in_db(
            db::USERS,
            db::users::USER_NAME.into(),
            db_id,
        ))?,
        creation: Time {
            utc: creation_time_utc,
        },
    })
}
