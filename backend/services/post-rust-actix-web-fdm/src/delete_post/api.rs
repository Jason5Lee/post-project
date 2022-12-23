use crate::common::*;
use crate::common::api::*;
use actix_web::{delete, web::Path as UrlPath, HttpResponse};
use actix_web::http::StatusCode;
use apply::Apply;
use crate::common::utils::error::*;

#[delete("/post/{id}")]
pub async fn api(mut ctx: utils::Context) -> Result<HttpResponse> {
    let caller = ctx.get_identity()?
        .ok_or_else(forbidden)?;
    let (id,): (String,) = ctx
        .to::<UrlPath<(String,)>>()
        .await
        .map_err(bad_request)?
        .to_owned();
    let input = utils::parse_id(&id)
        .map_err(|_| post_not_found())?
        .apply(PostId);
    super::Steps::from_ctx(&ctx).workflow(caller, input).await?;
    Ok(HttpResponse::NoContent().finish())
}

pub fn forbidden() -> ErrorResponse {
    (StatusCode::FORBIDDEN, ErrorBody {
        error: ErrBody {
            error: "FORBIDDEN".into(),
            reason: "the user is neither the creator of the post nor admin".to_string(),
            message: "you are neither the creator of the post nor admin".to_string()
        }
    }).into()
}

pub fn post_not_found() -> ErrorResponse {
    (StatusCode::NOT_FOUND, ErrorBody {
        error: ErrBody {
            error: "POST_NOT_FOUND".into(),
            reason: "post not found".to_string(),
            message: "post not found".to_string()
        }
    }).into()
}

