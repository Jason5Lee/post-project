use crate::common::utils::error::*;

impl super::super::TextPostContent {
    pub(in crate::common) fn text_post_content_empty() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "TEXT_POST_CONTENT_EMPTY".into(),
                reason: "text post content is empty".to_string(),
                message: "text post content is empty".to_string(),
            },
        }
    }
    pub(in crate::common) fn text_post_content_too_long() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "TEXT_POST_CONTENT_TOO_LONG".into(),
                reason: "text post content is too long".to_string(),
                message: "text post content is too long".to_string(),
            },
        }
    }
}
