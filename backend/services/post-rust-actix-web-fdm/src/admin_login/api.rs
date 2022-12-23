use super::*;
use crate::common::*;
use crate::common::api::bad_request;
use actix_web::HttpResponse;
use actix_web::{post, web::Json as BodyJson};
use actix_web::http::StatusCode;
use serde::Deserialize;
use serde::Serialize;
use crate::common::utils::error::*;

#[post("/admin/login")]
pub async fn api(mut ctx: utils::Context) -> Result<HttpResponse> {
    #[derive(Deserialize)]
    pub struct RequestDto {
        pub id: String,
        pub password: String,
    }
    let req_body = ctx.to::<BodyJson<RequestDto>>().await.map_err(bad_request)?.0;
    let input = Query {
        id: AdminId(utils::parse_id(&req_body.id)
            .map_err(|_| id_or_password_incorrect())?),
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
    (StatusCode::FORBIDDEN, ErrorBody {
        error: ErrBody {
            error: "ID_OR_PASSWORD_INCORRECT".into(),
            reason: "The admin ID does not exist, or the password is incorrect".to_string(),
            message: "Admin ID or password incorrect".to_string()
        }
    }).into()
}
