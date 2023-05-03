use super::*;
use crate::common::api::*;
use crate::common::utils::error::*;

use actix_web::http::StatusCode;
use actix_web::post;
use actix_web::web::Json as BodyJson;
use actix_web::HttpResponse;
use serde::{Deserialize, Serialize};

#[post("/post")]
pub async fn api(mut ctx: utils::Context) -> Result<HttpResponse> {
    let caller = match ctx.get_caller_identity()? {
        Some(Identity::User(user_id)) => user_id,
        _ => return Err(user_only()),
    };

    #[derive(Deserialize)]
    pub struct RequestDto {
        pub title: String,
        pub text: Option<String>,
        pub url: Option<String>,
    }

    let req = ctx
        .get::<BodyJson<RequestDto>>()
        .await
        .map_err(bad_request)?
        .0;
    let input = Command {
        title: Title::try_new(req.title).map_err(as_unprocessable_entity)?,
        content: match (req.text, req.url) {
            (Some(text), None) => {
                PostContent::Text(TextPostContent::try_new(text).map_err(as_unprocessable_entity)?)
            }
            (None, Some(url)) => {
                PostContent::Url(UrlPostContent::try_new(url).map_err(as_unprocessable_entity)?)
            }
            _ => return Err(text_url_exact_one()),
        },
    };
    let output = super::Steps::from_ctx(&ctx).workflow(caller, input).await?;
    let post_id = output.0;
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
    (
        StatusCode::CONFLICT,
        ErrorBody {
            error: ErrBody {
                error: "DUPLICATE_TITLE".into(),
                reason: "The title is already used".to_string(),
                message: "The title is already used".to_string(),
            },
        },
    )
        .into()
}

pub fn text_url_exact_one() -> ErrorResponse {
    (
        StatusCode::UNPROCESSABLE_ENTITY,
        ErrorBody {
            error: ErrBody {
                error: "TEXT_URL_EXACT_ONE".into(),
                reason: "The text and url fields must be exactly one".to_string(),
                message: CLIENT_BUG_MESSAGE.to_string(),
            },
        },
    )
        .into()
}
