use super::*;
use crate::common::api::{handle_internal_error, invalid_auth, low_probability_error};
use crate::common::{utils::Deps, *};

pub async fn workflow(deps: &Deps, creator: UserId, input: Command) -> Result<PostId> {
    let db_creator = db::parse_id(&creator.0).ok_or_else(invalid_auth)?;
    let db_id = deps.id_gen.lock().real_time_generate() as u64;
    let (text, url) = match input.content {
        PostContent::Text(text) => (Some(text.into_string()), None),
        PostContent::Url(url) => (None, Some(url.into_string())),
    };
    sqlx::query(&iformat!("INSERT INTO `" db::POSTS "` (`" db::posts::POST_ID "`,`" db::posts::CREATOR "`,`" db::posts::CREATION_TIME "`,`" db::posts::TITLE "`,`" db::posts::TEXT "`,`" db::posts::URL "`) VALUES (?,?,?,?,?,?)"))
        .bind(db_id)
        .bind(db_creator)
        .bind(Time::now().utc)
        .bind(input.title.as_str())
        .bind(text)
        .bind(url)
        .execute(&deps.pool)
        .await
        .map_err(|err|
            if db::is_unique_violation_in(&err, db::posts::TITLE) {
                duplicate_title()
            } else if db::is_unique_violation_in(&err, db::posts::POST_ID) {
                low_probability_error()
            } else {
                handle_internal_error(err)
            }
        )
        .map(|_| PostId(db::format_id(db_id)))
}
