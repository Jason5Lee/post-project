use crate::common::utils::error::*;

impl super::super::UrlPostContent {
    pub(in crate::common) fn url_post_content_empty() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "URL_POST_CONTENT_EMPTY".into(),
                reason: "url is empty".to_string(),
                message: "url is empty".to_string(),
            },
        }
    }
    pub(in crate::common) fn url_post_content_too_long() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "URL_POST_CONTENT_TOO_LONG".into(),
                reason: "url is too long".to_string(),
                message: "url is too long".to_string(),
            },
        }
    }
    pub(in crate::common) fn url_post_content_invalid(err: url::ParseError) -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "URL_POST_CONTENT_INVALID".into(),
                reason: format!("{}", err),
                message: format!("url is invalid: {}", err),
            },
        }
    }
}
