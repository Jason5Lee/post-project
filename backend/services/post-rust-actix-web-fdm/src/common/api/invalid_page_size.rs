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

    pub(in crate::common) fn page_size_too_large() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "PAGE_SIZE_TOO_LARGE".into(),
                reason: "page size is too large".to_string(),
                message: "page size is too large".to_string(),
            },
        }
    }
}
