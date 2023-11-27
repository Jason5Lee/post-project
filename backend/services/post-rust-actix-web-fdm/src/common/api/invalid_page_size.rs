use crate::common::utils::error::*;

impl super::super::PageSize {
    pub(in crate::common) fn invalid_page_size() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "INVALID_PAGE_SIZE".into(),
                reason: "page size is invalid".to_string(),
                message: "page size is invalid".to_string(),
            },
        }
    }
}
