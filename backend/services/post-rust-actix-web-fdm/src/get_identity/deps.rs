use crate::common::api::{handle_internal_error, invalid_auth};
use crate::common::utils::error::handle_invalid_value_in_db;
use crate::common::*;

pub async fn get_user_name(deps: &utils::Deps, id: &UserId) -> Result<UserName> {
    let db_id = db::parse_id(&id.0).ok_or_else(invalid_auth)?;
    let (name,): (String,) = sqlx::query_as(&iformat!("SELECT `" db::users::USER_NAME "` FROM `" db::USERS "` WHERE `" db::users::USER_ID "`=?"))
        .bind(db_id)
        .fetch_optional(&deps.pool)
        .await
        .map_err(handle_internal_error)?
        .ok_or_else(invalid_auth)?;

    UserName::try_new(name).map_err(handle_invalid_value_in_db(
        db::USERS,
        db::users::USER_NAME.into(),
        db_id,
    ))
}
