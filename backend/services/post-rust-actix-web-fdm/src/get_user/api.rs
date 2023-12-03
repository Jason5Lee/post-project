use crate::common::api::bad_request;
use crate::common::utils::error::*;
use crate::common::utils::{Endpoint, HttpMethod};
use crate::common::*;

use actix_web::http::StatusCode;
use actix_web::{web::Path as UrlPath, HttpResponse};
use serde::Serialize;
use std::rc::Rc;

pub const ENDPOINT: Endpoint = (HttpMethod::GET, "/user/{id}");
pub async fn api(mut ctx: utils::Context) -> Result<HttpResponse> {
    let (id,) = ctx
        .get::<UrlPath<(String,)>>()
        .await
        .map_err(bad_request)?
        .to_owned();
    let input = UserId(id);
    let output = super::Steps::from_ctx(&ctx).workflow(input).await?;
    Ok(HttpResponse::Ok().json({
        #[derive(Serialize)]
        #[allow(non_snake_case)]
        pub struct ResponseDto {
            pub userName: Rc<str>,
            pub creationTime: u64,
        }

        ResponseDto {
            userName: output.user_name.0,
            creationTime: output.creation.utc,
        }
    }))
}

pub fn user_not_found() -> ErrorResponse {
    (
        StatusCode::NOT_FOUND,
        ErrorBody {
            error: ErrBody {
                error: "USER_NOT_FOUND".into(),
                reason: "user not found".to_string(),
                message: "the user does not exist".to_string(),
            },
        },
    )
        .into()
}
