use super::*;
use crate::common::utils::id_generation::ID_DUPLICATE_MESSAGE;
use crate::common::{api::handle_internal_error, db::*};

pub async fn insert_user(
    deps: &utils::Deps,
    user_name: UserName,
    password: Password,
) -> Result<UserId> {
    let (id, now) = deps.user_id_gen.lock().unwrap().generate_id()?;
    sqlx::query(&format!(
        "INSERT INTO `{USER}` (`{USER_USER_ID}`,`{USER_USER_NAME}`,`{USER_ENCRYPTED_PASSWORD}`,`{USER_CREATION_TIME}`) VALUES (?,?,?,?)"
    ))
        .bind(id)
        .bind(&user_name.0 as &str)
        .bind(password.to_encrypted(&deps.encryptor)?)
        .bind(now.utc)
        .execute(&deps.pool)
        .await
        .map(|_| UserId(db::format_id(id)))
        .map_err(|err|
            match analysis_unique_violation_error(&err) {
                Some(UniqueViolationError::PrimaryKey) => handle_internal_error(ID_DUPLICATE_MESSAGE),
                Some(UniqueViolationError::OtherColumn) => user_name_already_exists(),
                None => handle_internal_error(err),
            }
        )
}
