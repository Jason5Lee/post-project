use super::Query;
use super::*;
use crate::common::api::*;
use crate::common::db::*;
use crate::common::*;

pub async fn workflow(deps: &utils::Deps, input: Query) -> Result<AdminId> {
    let db_id = db::parse_id(&input.id.0).ok_or_else(id_or_password_incorrect)?;

    let (encrypted_password,): (String,) = sqlx::query_as(&format!(
        "SELECT `{ADMIN_ENCRYPTED_PASSWORD}` FROM `{ADMIN}` WHERE `{ADMIN_ADMIN_ID}`=?"
    ))
    .bind(db_id)
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
