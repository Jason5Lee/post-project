use crate::common::utils::error::{ErrBody, ErrorBody, ErrorResponse};
use crate::common::utils::Context;
use crate::common::Result;
use actix_web::http::StatusCode;

const AUTHORIZATION: &str = "Authorization";
pub fn get_auth_token(ctx: &Context) -> Result<Option<&[u8]>> {
    match ctx.request.headers().get(AUTHORIZATION) {
        None => Ok(None),
        Some(header_value) => match header_value.as_bytes().strip_prefix(b"Bearer ") {
            None => Err(invalid_token()),
            Some(token) => Ok(Some(token)),
        },
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
                reason: format!("internal server error, trace id: {trace_id}"),
                message: "something went wrong".to_string(),
            },
        },
    )
        .into()
}

pub fn invalid_token() -> ErrorResponse {
    (
        StatusCode::UNAUTHORIZED,
        ErrorBody {
            error: ErrBody {
                error: "INVALID_TOKEN".into(),
                reason: "authorization token is invalid".to_string(),
                message: CLIENT_BUG_MESSAGE.to_string(),
            },
        },
    )
        .into()
}

pub fn forbidden() -> ErrorResponse {
    (
        StatusCode::FORBIDDEN,
        ErrorBody {
            error: ErrBody {
                error: "FORBIDDEN".into(),
                reason: "the user is not allowed to perform this action".to_string(),
                message: "you are not allowed to perform this action".to_string(),
            },
        },
    )
        .into()
}

impl super::UserName {
    pub(super) fn user_name_empty() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "USER_NAME_EMPTY".into(),
                reason: "user name is empty".to_string(),
                message: "user name is empty".to_string(),
            },
        }
    }
    pub(super) fn user_name_too_short() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "USER_NAME_TOO_SHORT".into(),
                reason: "user name is too short".to_string(),
                message: "user name is too short".to_string(),
            },
        }
    }
    pub(super) fn user_name_too_long() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "USER_NAME_TOO_LONG".into(),
                reason: "user name is too long".to_string(),
                message: "user name is too long".to_string(),
            },
        }
    }
    pub(super) fn user_name_contains_illegal_character() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "USER_NAME_ILLEGAL".into(),
                reason: "user name contains illegal character".to_string(),
                message: "user name contains illegal character".to_string(),
            },
        }
    }
}

impl super::Title {
    pub(super) fn title_empty() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "TITLE_EMPTY".into(),
                reason: "title is empty".to_string(),
                message: "title is empty".to_string(),
            },
        }
    }
    pub(super) fn title_too_short() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "TITLE_TOO_SHORT".into(),
                reason: "title is too short".to_string(),
                message: "title is too short".to_string(),
            },
        }
    }
    pub(super) fn title_too_long() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "TITLE_TOO_LONG".into(),
                reason: "title is too long".to_string(),
                message: "title is too long".to_string(),
            },
        }
    }
}

impl super::TextPostContent {
    pub(super) fn text_post_content_empty() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "TEXT_POST_CONTENT_EMPTY".into(),
                reason: "text post content is empty".to_string(),
                message: "text post content is empty".to_string(),
            },
        }
    }
    pub(super) fn text_post_content_too_long() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "TEXT_POST_CONTENT_TOO_LONG".into(),
                reason: "text post content is too long".to_string(),
                message: "text post content is too long".to_string(),
            },
        }
    }
}

impl super::UrlPostContent {
    pub(super) fn url_post_content_empty() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "URL_POST_CONTENT_EMPTY".into(),
                reason: "url is empty".to_string(),
                message: "url is empty".to_string(),
            },
        }
    }
    pub(super) fn url_post_content_too_long() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "URL_POST_CONTENT_TOO_LONG".into(),
                reason: "url is too long".to_string(),
                message: "url is too long".to_string(),
            },
        }
    }
    pub(super) fn url_post_content_invalid(err: url::ParseError) -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "URL_POST_CONTENT_INVALID".into(),
                reason: format!("{}", err),
                message: format!("url is invalid: {}", err),
            },
        }
    }
}

impl super::Password {
    pub(super) fn password_empty() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "PASSWORD_EMPTY".into(),
                reason: "password is empty".to_string(),
                message: "password is empty".to_string(),
            },
        }
    }
    pub(super) fn password_too_short() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "PASSWORD_TOO_SHORT".into(),
                reason: "password is too short".to_string(),
                message: "password is too short".to_string(),
            },
        }
    }
    pub(super) fn password_too_long() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "PASSWORD_TOO_LONG".into(),
                reason: "password is too long".to_string(),
                message: "password is too long".to_string(),
            },
        }
    }
}

impl super::Size {
    pub(super) fn size_non_positive() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "SIZE_NON_POSITIVE".into(),
                reason: "size should be a positive number".to_string(),
                message: "size should be a positive number".to_string(),
            },
        }
    }
}

pub fn invalid_id(reason: String) -> ErrorBody {
    ErrorBody {
        error: ErrBody {
            error: "INVALID_ID".into(),
            reason,
            message: "invalid ID".to_string(),
        },
    }
}

pub fn low_probability_error() -> ErrorResponse {
    (
        StatusCode::INTERNAL_SERVER_ERROR,
        ErrorBody {
            error: ErrBody {
                error: "LOW_PROBABILITY_ERROR".into(),
                reason: "an error that should be low-probability occurs".to_string(),
                message: "Something went wrong, please try again later".to_string(),
            },
        },
    )
        .into()
}
