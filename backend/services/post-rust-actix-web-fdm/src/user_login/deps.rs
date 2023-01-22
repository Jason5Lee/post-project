use super::*;
use crate::common::api::handle_internal_error;
use crate::common::utils::Deps;

pub async fn get_user_id_encrypted_password(
    deps: &Deps,
    user_name: UserName,
) -> Result<(UserId, String)> {
    let (id, encrypted_password): (u64, String) =
        sqlx::query_as(&iformat!("SELECT `" db::users::USER_ID "`,`" db::users::ENCRYPTED_PASSWORD "` FROM `" db::USERS "` WHERE `" db::users::USER_NAME "`=?"))
            .bind(user_name.as_str())
            .fetch_one(&deps.pool)
            .await
            .map_err(|err| match err {
                sqlx::Error::RowNotFound => user_name_or_password_incorrect(),
                _ => handle_internal_error(err),
            })?;
    Ok((UserId(db::format_id(id)), encrypted_password))
}
