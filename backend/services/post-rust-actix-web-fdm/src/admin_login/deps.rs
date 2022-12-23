use super::*;
use super::Query;
use crate::common::*;
use crate::common::api::*;

pub async fn workflow(deps: &utils::Deps, input: Query) -> Result<AdminId> {
    let (encrypted_password,): (String,) =
        sqlx::query_as(&iformat!("SELECT `" db::admin::ENCRYPTED_PASSWORD "` FROM `" db::ADMIN "` WHERE `" db::admin::ADMIN_ID "`=?"))
            .bind(input.id.0)
            .fetch_optional(&deps.pool)
            .await
            .map_err(handle_internal_error)?
            .ok_or_else(id_or_password_incorrect)?;
    if input.password.verify(&encrypted_password)? {
        Ok(input.id)
    } else {
        Err(id_or_password_incorrect())
    }
}
