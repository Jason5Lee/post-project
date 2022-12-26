use crate::common::api::handle_internal_error;
use crate::common::utils::error::handle_invalid_value_in_db;
use crate::common::*;

pub async fn get_user_name(deps: &utils::Deps, id: UserId) -> Result<UserName> {
    log::info!("id = {}", id.0);
    let (name,): (String,) = sqlx::query_as(&iformat!("SELECT `" db::users::USER_NAME "` FROM `" db::USERS "` WHERE `" db::users::USER_ID "`=?"))
        .bind(id.0)
        .fetch_one(&deps.pool)
        .await
        .map_err(handle_internal_error)?;
    UserName::try_new(name).map_err(handle_invalid_value_in_db(
        db::USERS,
        db::users::USER_NAME.into(),
        id.0,
    ))
}
