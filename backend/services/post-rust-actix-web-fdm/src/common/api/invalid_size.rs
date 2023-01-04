use crate::common::utils::error::*;

impl super::super::Size {
    pub(in crate::common) fn size_non_positive() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "SIZE_NON_POSITIVE_INTEGER".into(),
                reason: "size should be a positive integer".to_string(),
                message: "size should be a positive integer".to_string(),
            },
        }
    }
}
