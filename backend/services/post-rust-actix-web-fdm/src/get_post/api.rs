use std::rc::Rc;

use crate::common::utils::Context;
use actix_web::{get, web::Path as UrlPath, HttpResponse};
use actix_web::http::StatusCode;
use apply::Apply;
use serde::Serialize;
use crate::common::api::bad_request;
use crate::common::utils::error::*;

use super::*;

#[get("/post/{id}")]
pub async fn api(mut ctx: Context) -> Result<HttpResponse> {
    let (id,) = ctx.to::<UrlPath<(String,)>>().await.map_err(bad_request)?.to_owned();
    let input = utils::parse_id(&id)
        .map(PostId)
        .map_err(|_| super::post_not_found())?;
    let output = super::Steps::from_ctx(&ctx).workflow(input).await?;

    HttpResponse::Ok().json({
        #[derive(Serialize)]
        #[allow(non_snake_case)]
        pub struct ResponseDto {
            pub creatorId: String,
            pub creatorName: Rc<str>,
            pub creationTime: u64,
            pub title: String,
            #[serde(skip_serializing_if = "Option::is_none")]
            pub post: Option<String>,
            #[serde(skip_serializing_if = "Option::is_none")]
            pub url: Option<String>,
            #[serde(skip_serializing_if = "Option::is_none")]
            pub lastModified: Option<u64>,
        }

        let (post, url) = match output.content {
            PostContent::Post(post) => (Some(post), None),
            PostContent::Url(url) => (None, Some(url.to_string())),
        };

        ResponseDto {
            creatorId: utils::format_id(output.creator.id.0),
            creatorName: output.creator.name.into_rc_str(),
            creationTime: output.creation.utc,
            title: output.title.into_string(),
            post,
            url,
            lastModified: output.last_modified.map(|t| t.utc),
        }
    })
    .apply(Ok)
}

pub fn post_not_found() -> ErrorResponse {
    (StatusCode::NOT_FOUND, ErrorBody {
        error: ErrBody {
            error: "POST_NOT_FOUND".into(),
            reason: "post not found".to_string(),
            message: "the post does not exist".to_string()
        }
    }).into()
}
