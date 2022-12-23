use std::rc::Rc;
use crate::common::*;
use serde::Serialize;
use actix_web::{get, HttpResponse, web::Path as UrlPath};
use actix_web::http::StatusCode;
use crate::common::api::bad_request;
use crate::common::utils::error::*;

#[get("/user/{id}")]
pub async fn api(mut ctx: utils::Context) -> Result<HttpResponse> {
    let (id,) = ctx.to::<UrlPath<(String,)>>().await.map_err(bad_request)?.to_owned();
    let input = UserId(utils::parse_id(&id).map_err(|_| super::user_not_found())?);
    let output = super::Steps::from_ctx(&ctx).workflow(input).await?;
    Ok(HttpResponse::Ok().json({
        #[derive(Serialize)]
        #[allow(non_snake_case)]
        pub struct ResponseDto {
            pub userName: Rc<str>,
            pub creationTime: u64,
        }

        ResponseDto {
            userName: output.user_name.into_rc_str(),
            creationTime: output.creation.utc,
        }
    }))
}

pub fn user_not_found() -> ErrorResponse {
    (StatusCode::NOT_FOUND, ErrorBody {
        error: ErrBody {
            error: "USER_NOT_FOUND".into(),
            reason: "user not found".to_string(),
            message: "the user does not exist".to_string()
        }
    }).into()
}
