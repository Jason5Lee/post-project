pub mod invalid_password;
pub mod invalid_text_post_content;
pub mod invalid_title;
pub mod invalid_url_post_content;
pub mod invalid_user_name;
pub mod invalid_page;
pub mod invalid_page_size;

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

pub const CLIENT_BUG_MESSAGE: &str = "Something went wrong. Looks like a bug of the client. Please report this issue to the client implementation.";

pub fn bad_request(err: impl std::fmt::Display) -> ErrorResponse {
    (
        StatusCode::BAD_REQUEST,
        ErrorBody {
            error: ErrBody {
                error: "BAD_REQUEST".into(),
                reason: format!("{err}"),
                message: CLIENT_BUG_MESSAGE.to_string(),
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
                reason: format!("Internal server error, trace id: {trace_id}"),
                message: "Something went wrong".to_string(),
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
                reason: "Authorization is invalid".to_string(),
                message: CLIENT_BUG_MESSAGE.to_string(),
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
                message: "You are not allowed to perform this action".into(),
            },
        },
    )
        .into()
}

pub fn overloaded() -> ErrorResponse {
    (
        StatusCode::SERVICE_UNAVAILABLE,
        ErrorBody {
            error: ErrBody {
                error: "OVERLOADED".into(),
                reason: "The operation cannot be completed due to an excessive number of requests"
                    .to_string(),
                message: "Something went wrong, please try again later".to_string(),
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
            message: CLIENT_BUG_MESSAGE.into(),
        },
    })
}
