use crate::common::utils::error::*;

impl super::super::Page {
    pub(in crate::common) fn invalid_page() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "INVALID_PAGE".into(),
                reason: "page is invalid".to_string(),
                message: "page is invalid".to_string(),
            },
        }
    }
}
