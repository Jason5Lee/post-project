use super::Command;
use crate::common::api::bad_request;
use crate::common::utils::error::*;
use crate::common::*;
use crate::common::utils::{Endpoint, HttpMethod};
use actix_web::http::StatusCode;
use actix_web::{web::Json as BodyJson, HttpResponse, Result};
use serde::{Deserialize, Serialize};

pub const ENDPOINT: Endpoint = (HttpMethod::POST, "/register");
pub async fn api(mut ctx: utils::Context) -> Result<HttpResponse> {
    #[derive(Deserialize)]
    #[allow(non_snake_case)]
    pub struct RequestDto {
        pub userName: String,
        pub password: String,
    }

    let req = ctx
        .get::<BodyJson<RequestDto>>()
        .await
        .map_err(bad_request)?
        .0;
    let input = Command {
        user_name: UserName::try_new(req.userName).map_err(as_unprocessable_entity)?,
        password: Password::try_from_plain(req.password).map_err(as_unprocessable_entity)?,
    };
    let output = super::Steps::from_ctx(&ctx).workflow(input).await?;
    let user_id = output.0;
    Ok(HttpResponse::Created()
        .append_header(("Location", format!("/user/{user_id}")))
        .json({
            #[derive(Serialize)]
            #[allow(non_snake_case)]
            pub struct ResponseDto {
                pub userId: String,
            }

            ResponseDto { userId: user_id }
        }))
}

pub fn user_name_already_exists() -> ErrorResponse {
    (
        StatusCode::CONFLICT,
        ErrorBody {
            error: ErrBody {
                error: "USER_NAME_ALREADY_EXISTS".into(),
                reason: "the user name already exists".to_string(),
                message: "the user name already exists".to_string(),
            },
        },
    )
        .into()
}
