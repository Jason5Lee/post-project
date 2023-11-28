use std::rc::Rc;

use super::*;
use crate::common::api::bad_request;
use crate::common::utils::error::*;
use crate::common::utils::Context;
use crate::common::utils::{Endpoint, HttpMethod};

use actix_web::http::StatusCode;
use actix_web::{web::Query as QueryString, HttpResponse};
use apply::Apply;
use serde::{Deserialize, Serialize};

pub const ENDPOINT: Endpoint = (HttpMethod::GET, "/post");
pub async fn api(mut ctx: Context) -> Result<HttpResponse> {
    #[derive(Deserialize)]
    #[allow(non_snake_case)]
    pub struct RequestDto {
        pub page: u64,
        pub pageSize: u64,
        pub creator: Option<String>,
    }
    let req = ctx
        .get::<QueryString<RequestDto>>()
        .await
        .map_err(bad_request)?
        .0;
    let input = Query {
        creator: req.creator.map(UserId),
        page: Page::try_new(req.page).map_err(as_unprocessable_entity)?,
        page_size: PageSize::try_new(req.pageSize).map_err(as_unprocessable_entity)?,
    };
    let output = super::Steps::from_ctx(&ctx).workflow(input).await?;

    HttpResponse::Ok()
        .json({
            #[derive(Serialize)]
            #[allow(non_snake_case)]
            pub struct ResponseDto {
                pub total: u64,
                pub posts: Vec<PostInfoDto>,
            }

            #[derive(Serialize)]
            #[allow(non_snake_case)]
            pub struct PostInfoDto {
                pub id: String,
                pub title: String,
                pub creatorId: String,
                pub creatorName: Rc<str>,
                pub creationTime: u64,
            }

            ResponseDto {
                total: output.total,
                posts: output
                    .posts
                    .into_iter()
                    .map(|output| PostInfoDto {
                        id: output.id.0,
                        title: output.title.0,
                        creatorId: output.creator.id.0,
                        creatorName: output.creator.name.0,
                        creationTime: output.creation.utc,
                    })
                    .collect::<Vec<_>>(),
            }
        })
        .apply(Ok)
}

pub fn creator_not_found() -> ErrorResponse {
    (
        StatusCode::NOT_FOUND,
        ErrorBody {
            error: ErrBody {
                error: "CREATOR_NOT_FOUND".into(),
                reason: "creator not found".to_string(),
                message: "the creator does not exist".to_string(),
            },
        },
    )
        .into()
}
