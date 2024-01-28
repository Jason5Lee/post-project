use super::*;
use crate::common::api::bad_request;
use crate::common::utils;
use crate::common::utils::error::*;
use crate::common::utils::{Endpoint, HttpMethod};
use actix_web::http::StatusCode;
use actix_web::{web::Json as BodyJson, HttpResponse};
use serde::{Deserialize, Serialize};

pub const ENDPOINT: Endpoint = (HttpMethod::POST, "/login");
pub async fn api(mut ctx: utils::Context) -> Result<HttpResponse> {
    #[derive(Deserialize)]
    #[allow(non_snake_case)]
    pub struct RequestDto {
        pub userName: String,
        pub password: String,
    }
    let req_body = ctx
        .get::<BodyJson<RequestDto>>()
        .await
        .map_err(bad_request)?
        .0;
    let input = Query {
        user_name: UserName::try_new(req_body.userName)
            .ok_or_else(super::user_name_or_password_incorrect)?,
        password: Password::try_from_plain(req_body.password)
            .ok_or_else(super::user_name_or_password_incorrect)?,
    };
    let output = super::Steps::from_ctx(&ctx).workflow(input).await?;
    Ok(HttpResponse::Ok().json({
        let expired_time = ctx.get_token_expire_time();
        #[derive(Serialize)]
        #[allow(non_snake_case)]
        pub struct ResponseDto {
            pub id: String,
            pub expire: u64,
            pub token: String,
        }
        ResponseDto {
            id: output.0.clone(),
            expire: expired_time.utc,
            token: ctx.generate_user_token(expired_time, output),
        }
    }))
}

pub fn user_name_or_password_incorrect() -> ErrorResponse {
    (
        StatusCode::FORBIDDEN,
        ErrorBody {
            error: ErrBody {
                error: "USER_NAME_OR_PASSWORD_INCORRECT".into(),
                reason: "The user name does not exist, or the password is incorrect".into(),
            },
        },
    )
        .into()
}
