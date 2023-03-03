use super::*;
use crate::common::api::{handle_internal_error, invalid_auth, low_probability_error};
use crate::common::{db::*, utils::Deps, *};

pub async fn workflow(deps: &Deps, creator: UserId, input: Command) -> Result<PostId> {
    let db_creator = db::parse_id(&creator.0).ok_or_else(invalid_auth)?;
    let db_id = deps.id_gen.lock().real_time_generate() as u64;
    let (text, url) = match input.content {
        PostContent::Text(text) => (Some(text.0), None),
        PostContent::Url(url) => (None, Some(url.0)),
    };
    sqlx::query(&format!("INSERT INTO `{POST}` (`{POST_POST_ID}`,`{POST_CREATOR}`,`{POST_CREATION_TIME}`,`{POST_TITLE}`,`{POST_TEXT}`,`{POST_URL}`) VALUES (?,?,?,?,?,?)"))
        .bind(db_id)
        .bind(db_creator)
        .bind(Time::now().utc)
        .bind(&input.title.0)
        .bind(text)
        .bind(url)
        .execute(&deps.pool)
        .await
        .map_err(|err|
            match analysis_unique_violation_error(&err) {
                Some(UniqueViolationError::PrimaryKey) => low_probability_error(),
                Some(UniqueViolationError::OtherColumn) => duplicate_title(),
                None => handle_internal_error(err)
            }
        )
        .map(|_| PostId(db::format_id(db_id)))
}
