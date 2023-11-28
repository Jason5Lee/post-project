use super::*;
use crate::common::api::{handle_internal_error, invalid_auth};
use crate::common::{db::*, utils::Deps, *};

pub async fn workflow(deps: &Deps, creator: UserId, input: Command) -> Result<PostId> {
    let db_creator = db::parse_id(&creator.0).ok_or_else(invalid_auth)?;

    // let (db_id, now) = (uuid::new_v4(), chrono::Utc::now());
    let (text, url) = match input.content {
        PostContent::Text(text) => (Some(text.0), None),
        PostContent::Url(url) => (None, Some(url.0)),
    };
    sqlx::query(&format!("INSERT INTO `{POST}` (`{POST_CREATOR}`,`{POST_CREATION_TIME}`,`{POST_TITLE}`,`{POST_TEXT}`,`{POST_URL}`) VALUES (?,?,?,?,?)"))
        .bind(db_creator)
        .bind(utils::current_timestamp_utc())
        .bind(&input.title.0)
        .bind(text)
        .bind(url)
        .execute(&deps.pool)
        .await
        .map_err(|err|
            match analysis_unique_violation_error(&err) {
                Some(UniqueViolationError::PrimaryKey) => handle_internal_error(ID_DUPLICATE_MESSAGE),
                Some(UniqueViolationError::OtherColumn) => duplicate_title(),
                None => handle_internal_error(err)
            }
        )
        .map(|result| PostId(db::format_id(result.last_insert_id())))
}
