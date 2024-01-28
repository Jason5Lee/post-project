use super::*;
use crate::common::api::handle_internal_error;
use crate::common::db::*;
use crate::common::utils::Deps;

pub async fn get_user_id_encrypted_password(
    deps: &Deps,
    user_name: UserName,
) -> Result<(UserId, String)> {
    let (id, encrypted_password): (u64, String) =
        sqlx::query_as(&format!("SELECT `{USER_USER_ID}`,`{USER_ENCRYPTED_PASSWORD}` FROM `{USER}` WHERE `{USER_USER_NAME}`=?"))
            .bind(&user_name.0 as &str)
            .fetch_one(&deps.pool)
            .await
            .map_err(|err| match err {
                sqlx::Error::RowNotFound => user_name_or_password_incorrect(),
                _ => handle_internal_error(err),
            })?;
    Ok((UserId(db::format_id(id)), encrypted_password))
}
