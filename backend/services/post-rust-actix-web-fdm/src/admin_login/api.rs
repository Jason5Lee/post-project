use super::*;
use crate::common::api::bad_request;
use crate::common::utils::error::*;
use crate::common::*;
use actix_web::http::StatusCode;
use actix_web::HttpResponse;
use actix_web::{post, web::Json as BodyJson};
use serde::Deserialize;
use serde::Serialize;

#[post("/admin/login")]
pub async fn api(mut ctx: utils::Context) -> Result<HttpResponse> {
    #[derive(Deserialize)]
    pub struct RequestDto {
        pub id: String,
        pub password: String,
    }
    let req_body = ctx
        .get::<BodyJson<RequestDto>>()
        .await
        .map_err(bad_request)?
        .0;
    let input = Query {
        id: AdminId(req_body.id),
        password: Password::try_from_plain(req_body.password)
            .map_err(|_| id_or_password_incorrect())?,
    };
    let output = super::Steps::from_ctx(&ctx).workflow(input).await?;
    Ok(HttpResponse::Ok().json({
        #[derive(Serialize)]
        pub struct ResponseDto {
            expire: u64,
            token: String,
        }
        let expire = ctx.get_token_expire_time();
        ResponseDto {
            expire: expire.utc,
            token: ctx.generate_token(expire, Identity::Admin(output)),
        }
    }))
}

pub fn id_or_password_incorrect() -> ErrorResponse {
    (
        StatusCode::FORBIDDEN,
        ErrorBody {
            error: ErrBody {
                error: "ID_OR_PASSWORD_INCORRECT".into(),
                reason: "The Admin ID or password is incorrect".to_string(),
                message: "The Admin ID or password is incorrect".to_string(),
            },
        },
    )
        .into()
}
