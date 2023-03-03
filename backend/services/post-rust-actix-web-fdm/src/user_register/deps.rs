use super::*;
use crate::common::{
    api::{handle_internal_error, low_probability_error},
    db::*,
};

pub async fn insert_user(
    deps: &utils::Deps,
    user_name: UserName,
    password: Password,
) -> Result<UserId> {
    let id = deps.id_gen.lock().real_time_generate() as u64;
    sqlx::query(&format!(
        "INSERT INTO `{USER}` (`{USER_USER_ID}`,`{USER_USER_NAME}`,`{USER_ENCRYPTED_PASSWORD}`,`{USER_CREATION_TIME}`) VALUES (?,?,?,?)"
    ))
        .bind(id)
        .bind(&user_name.0 as &str)
        .bind(password.to_encrypted(&deps.encryptor)?)
        .bind(Time::now().utc)
        .execute(&deps.pool)
        .await
        .map(|_| UserId(db::format_id(id)))
        .map_err(|err|
            match analysis_unique_violation_error(&err) {
                Some(UniqueViolationError::PrimaryKey) => low_probability_error(),
                Some(UniqueViolationError::OtherColumn) => super::user_name_already_exists(),
                None => handle_internal_error(err),
            }
        )
}
