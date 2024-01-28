use std::rc::Rc;

use crate::common::api::bad_request;
use crate::common::utils::error::*;
use crate::common::utils::Context;
use crate::common::utils::{Endpoint, HttpMethod};
use actix_web::http::StatusCode;
use actix_web::{web::Path as UrlPath, HttpResponse};
use apply::Apply;
use serde::Serialize;

use super::*;

pub const ENDPOINT: Endpoint = (HttpMethod::GET, "/post/{id}");
pub async fn api(mut ctx: Context) -> Result<HttpResponse> {
    let (id,) = ctx
        .get::<UrlPath<(String,)>>()
        .await
        .map_err(bad_request)?
        .to_owned();
    let input = PostId(id);
    let output = super::Steps::from_ctx(&ctx).workflow(input).await?;

    HttpResponse::Ok()
        .json({
            #[derive(Serialize)]
            #[allow(non_snake_case)]
            pub struct ResponseDto {
                pub creatorId: String,
                pub creatorName: Rc<str>,
                pub creationTime: u64,
                pub title: String,
                #[serde(skip_serializing_if = "Option::is_none")]
                pub text: Option<String>,
                #[serde(skip_serializing_if = "Option::is_none")]
                pub url: Option<String>,
                #[serde(skip_serializing_if = "Option::is_none")]
                pub lastModified: Option<u64>,
            }

            let (text, url) = match output.content {
                PostContent::Text(text) => (Some(text.0), None),
                PostContent::Url(url) => (None, Some(url.0)),
            };

            ResponseDto {
                creatorId: output.creator.id.0,
                creatorName: output.creator.name.0,
                creationTime: output.creation.utc,
                title: output.title.0,
                text,
                url,
                lastModified: output.last_modified.map(|t| t.utc),
            }
        })
        .apply(Ok)
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
