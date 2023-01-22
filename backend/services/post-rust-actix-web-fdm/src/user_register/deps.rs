use super::*;
use crate::common::api::{handle_internal_error, low_probability_error};

pub async fn insert_user(
    deps: &utils::Deps,
    user_name: UserName,
    password: Password,
) -> Result<UserId> {
    let id = deps.id_gen.lock().real_time_generate() as u64;
    sqlx::query(&iformat!(
        "INSERT INTO `" db::USERS "` (`" db::users::USER_ID "`,`" db::users::USER_NAME "`,`" db::users::ENCRYPTED_PASSWORD "`,`" db::users::CREATION_TIME "`) VALUES (?,?,?,?)"
    ))
        .bind(id)
        .bind(user_name.as_str())
        .bind(password.to_encrypted(&deps.encryptor)?)
        .bind(Time::now().utc)
        .execute(&deps.pool)
        .await
        .map(|_| UserId(db::format_id(id)))
        .map_err(|err|
            if db::is_unique_violation_in(&err, db::users::USER_NAME) {
                super::user_name_already_exists()
            } else if db::is_unique_violation_in(&err, db::users::USER_ID) {
                low_probability_error()
            } else {
                handle_internal_error(err)
            }
        )
}
