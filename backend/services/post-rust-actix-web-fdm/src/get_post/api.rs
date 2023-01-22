use std::rc::Rc;

use crate::common::api::bad_request;
use crate::common::utils::error::*;
use crate::common::utils::Context;
use actix_web::http::StatusCode;
use actix_web::{get, web::Path as UrlPath, HttpResponse};
use apply::Apply;
use serde::Serialize;

use super::*;

#[get("/post/{id}")]
pub async fn api(mut ctx: Context) -> Result<HttpResponse> {
    let (id,) = ctx
        .to::<UrlPath<(String,)>>()
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
                PostContent::Text(text) => (Some(text.into_string()), None),
                PostContent::Url(url) => (None, Some(url.into_string())),
            };

            ResponseDto {
                creatorId: output.creator.id.0,
                creatorName: output.creator.name.into_rc_str(),
                creationTime: output.creation.utc,
                title: output.title.into_string(),
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
                reason: "post not found".to_string(),
                message: "the post does not exist".to_string(),
            },
        },
    )
        .into()
}
