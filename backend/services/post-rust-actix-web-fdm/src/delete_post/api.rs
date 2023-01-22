use crate::common::api::*;
use crate::common::utils::error::*;
use crate::common::*;
use actix_web::http::StatusCode;
use actix_web::{delete, web::Path as UrlPath, HttpResponse};

#[delete("/post/{id}")]
pub async fn api(mut ctx: utils::Context) -> Result<HttpResponse> {
    let caller = ctx.get_caller_identity()?.ok_or_else(not_creator_admin)?;
    let (id,): (String,) = ctx
        .to::<UrlPath<(String,)>>()
        .await
        .map_err(bad_request)?
        .to_owned();
    let input = PostId(id);
    super::Steps::from_ctx(&ctx).workflow(caller, input).await?;
    Ok(HttpResponse::NoContent().finish())
}

pub fn not_creator_admin() -> ErrorResponse {
    (
        StatusCode::FORBIDDEN,
        ErrorBody {
            error: ErrBody {
                error: "NOT_CREATOR_ADMIN".into(),
                reason: "the user is neither the creator of the post nor admin".to_string(),
                message: "you are neither the creator of the post nor admin".to_string(),
            },
        },
    )
        .into()
}

pub fn post_not_found() -> ErrorResponse {
    (
        StatusCode::NOT_FOUND,
        ErrorBody {
            error: ErrBody {
                error: "POST_NOT_FOUND".into(),
                reason: "post not found".to_string(),
                message: "post not found".to_string(),
            },
        },
    )
        .into()
}
