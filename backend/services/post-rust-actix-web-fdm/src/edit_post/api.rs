use super::*;
use crate::common::api::*;
use crate::common::utils::error::*;
use crate::common::utils::{Endpoint, HttpMethod};
use crate::common::*;
use actix_web::http::StatusCode;
use actix_web::{web::Json as BodyJson, web::Path as UrlPath, HttpResponse};
use serde::Deserialize;

use crate::common::api::invalidation::{invalid_text_post_content, invalid_url_post_content};

pub const ENDPOINT: Endpoint = (HttpMethod::PATCH, "/post/{id}");
pub async fn api(mut ctx: utils::Context) -> Result<HttpResponse> {
    let caller = match ctx.get_caller_identity()? {
        Some(Identity::User(user_id)) => user_id,
        _ => return Err(not_creator()),
    };
    #[derive(Deserialize)]
    #[allow(non_snake_case)]
    pub struct RequestDto {
        pub text: Option<String>,
        pub url: Option<String>,
    }

    let req = ctx
        .get::<BodyJson<RequestDto>>()
        .await
        .map_err(bad_request)?
        .0;
    let input = Command {
        id: PostId(
            ctx.get::<UrlPath<(String,)>>()
                .await
                .map_err(bad_request)?
                .to_owned()
                .0,
        ),
        new_content: match (req.text, req.url) {
            (Some(text), None) => PostContent::Text(
                TextPostContent::try_new(text)
                    .ok_or_else(|| (StatusCode::BAD_REQUEST, invalid_text_post_content()))?,
            ),
            (None, Some(url)) => PostContent::Url(
                UrlPostContent::try_new(url)
                    .ok_or_else(|| (StatusCode::BAD_REQUEST, invalid_url_post_content()))?,
            ),
            _ => return Err(test_url_exact_one()),
        },
    };
    super::Steps::from_ctx(&ctx).workflow(caller, input).await?;
    Ok(HttpResponse::NoContent().finish())
}

pub fn post_not_found() -> ErrorResponse {
    (
        StatusCode::NOT_FOUND,
        ErrorBody {
            error: ErrBody {
                error: "POST_NOT_FOUND".into(),
                reason: "Post not found".into(),
            },
        },
    )
        .into()
}

pub fn not_creator() -> ErrorResponse {
    (
        StatusCode::FORBIDDEN,
        ErrorBody {
            error: ErrBody {
                error: "NOT_CREATOR".into(),
                reason: "The user is not the creator of the post".into(),
            },
        },
    )
        .into()
}

pub fn type_diff() -> ErrorResponse {
    (
        StatusCode::BAD_REQUEST,
        ErrorBody {
            error: ErrBody {
                error: "TYPE_DIFF".into(),
                reason: "The type of the post is different from the request".into(),
            },
        },
    )
        .into()
}

pub fn test_url_exact_one() -> ErrorResponse {
    (
        StatusCode::BAD_REQUEST,
        ErrorBody {
            error: ErrBody {
                error: "TEXT_URL_EXACT_ONE".into(),
                reason: "Exact one of the text and the url field should exist".into(),
            },
        },
    )
        .into()
}
