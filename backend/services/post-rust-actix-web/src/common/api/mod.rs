pub mod invalidation;

use crate::common::utils::error::{ErrBody, ErrorBody, ErrorResponse};
use crate::common::utils::Context;
use crate::common::Result;
use actix_web::http::StatusCode;

const AUTHORIZATION: &str = "Authorization";
pub enum AuthToken<'a> {
    Guest,
    User(&'a [u8]),
    Admin(&'a [u8]),
}
pub fn get_auth_token(ctx: &Context) -> Result<AuthToken> {
    match ctx.request.headers().get(AUTHORIZATION) {
        None => Ok(AuthToken::Guest),
        Some(header_value) => {
            let header_value = header_value.as_bytes();
            if let Some(user_token) = header_value.strip_prefix(b"Bearer ") {
                Ok(AuthToken::User(user_token))
            } else if let Some(admin_token) = header_value.strip_prefix(b"Admin ") {
                Ok(AuthToken::Admin(admin_token))
            } else {
                Err(invalid_auth())
            }
        }
    }
}

pub fn bad_request(err: impl std::fmt::Display) -> ErrorResponse {
    (
        StatusCode::BAD_REQUEST,
        ErrorBody {
            error: ErrBody {
                error: "BAD_REQUEST".into(),
                reason: format!("{err}").into(),
            },
        },
    )
        .into()
}

#[track_caller]
pub fn handle_internal_error(err: impl std::fmt::Display) -> ErrorResponse {
    let trace_id = uuid::Uuid::new_v4();
    log::error!("[{}] {}", trace_id, err);

    (
        StatusCode::INTERNAL_SERVER_ERROR,
        ErrorBody {
            error: ErrBody {
                error: "INTERNAL_SERVER_ERROR".into(),
                reason: format!("Internal server error, trace id: {trace_id}").into(),
            },
        },
    )
        .into()
}

pub fn invalid_auth() -> ErrorResponse {
    (
        StatusCode::UNAUTHORIZED,
        ErrorBody {
            error: ErrBody {
                error: "INVALID_AUTH".into(),
                reason: "Authorization is invalid".into(),
            },
        },
    )
        .into()
}

pub fn user_only() -> ErrorResponse {
    (
        StatusCode::FORBIDDEN,
        ErrorBody {
            error: ErrBody {
                error: "USER_ONLY".into(),
                reason: "Only user can call this API".into(),
            },
        },
    )
        .into()
}

pub async fn api_not_found() -> actix_web::HttpResponse {
    actix_web::HttpResponse::NotFound().json(ErrorBody {
        error: ErrBody {
            error: "API_NOT_FOUND".into(),
            reason: "The API does not exist".into(),
        },
    })
}
