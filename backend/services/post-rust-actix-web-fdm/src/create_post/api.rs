use super::*;
use crate::common::api::*;
use crate::common::utils::error::*;

use actix_web::http::StatusCode;
use actix_web::put;
use actix_web::web::Json as BodyJson;
use actix_web::HttpResponse;
use serde::{Deserialize, Serialize};

#[put("/post")]
pub async fn api(mut ctx: utils::Context) -> Result<HttpResponse> {
    let caller = ctx.auth_user_only()?;

    #[derive(Deserialize)]
    pub struct RequestDto {
        pub title: String,
        pub post: Option<String>,
        pub url: Option<String>,
    }

    let req = ctx.to::<BodyJson<RequestDto>>().await.map_err(bad_request)?.0;
    let input = Command {
        title: Title::try_new(req.title).map_err(as_unprocessable_entity)?,
        content: match (req.post, req.url) {
            (Some(post), None) => PostContent::try_new_post(post).map_err(as_unprocessable_entity)?,
            (None, Some(url)) => PostContent::try_new_url(url).map_err(as_unprocessable_entity)?,
            _ => return Err((StatusCode::UNPROCESSABLE_ENTITY, ErrorBody {
                error: ErrBody {
                    error: "POST_AND_URL_SHOULD_HAVE_EXACTLY_ONE".into(),
                    reason: "post and url should have exactly one".to_string(),
                    message: "post and url should have exactly one".to_string(),
                },
            }).into()),
        },
    };
    let output = super::Steps::from_ctx(&ctx).workflow(caller, input).await?;
    let post_id = utils::format_id(output.0);
    Ok(HttpResponse::Created()
        .append_header(("Location", format!("/post/{}", post_id)))
        .json({
            #[derive(Serialize)]
            #[allow(non_snake_case)]
            pub struct ResponseDto {
                pub postId: String,
            }
            ResponseDto { postId: post_id }
        }))
}

pub fn duplicate_title() -> ErrorResponse {
    (StatusCode::CONFLICT, ErrorBody {
        error: ErrBody {
            error: "DUPLICATE_TITLE".into(),
            reason: "the post with the same title already exists".to_string(),
            message: "title already exists".to_string()
        }
    }).into()
}
