use std::rc::Rc;

use super::*;
use crate::common::api::{bad_request, CLIENT_BUG_MESSAGE};
use crate::common::utils::error::*;
use crate::common::utils::Context;
use actix_web::http::StatusCode;
use actix_web::{get, web::Query as QueryString, HttpResponse};
use apply::Apply;
use serde::{Deserialize, Serialize};

#[get("/post")]
pub async fn api(mut ctx: Context) -> Result<HttpResponse> {
    #[derive(Deserialize)]
    pub struct RequestDto {
        pub before: Option<u64>,
        pub after: Option<u64>,
        pub size: Option<u32>,
        pub creator: Option<String>,
    }
    let req = ctx
        .get::<QueryString<RequestDto>>()
        .await
        .map_err(bad_request)?
        .0;
    let input = Query {
        creator: match req.creator {
            None => None,
            Some(id) => Some(UserId(id)),
        },
        condition: match (req.before, req.after) {
            (None, None) => Condition::No,
            (Some(utc), None) => Condition::Before(Time { utc }),
            (None, Some(utc)) => Condition::After(Time { utc }),
            _ => {
                return Err(ErrorResponse {
                    status_code: StatusCode::UNPROCESSABLE_ENTITY,
                    body: ErrorBody {
                        error: ErrBody {
                            error: "BOTH_BEFORE_AFTER".into(),
                            reason: "only one of before and after should present".into(),
                            message: CLIENT_BUG_MESSAGE.into(),
                        },
                    },
                })
            }
        },
        size: Size::try_new(req.size).map_err(as_unprocessable_entity)?,
    };
    let output = super::Steps::from_ctx(&ctx).workflow(input).await?;

    HttpResponse::Ok()
        .json({
            #[derive(Serialize)]
            #[allow(non_snake_case)]
            pub struct ResponseDto {
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
