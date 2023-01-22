use crate::common::utils::error::*;

impl super::super::UserName {
    pub(in crate::common) fn user_name_empty() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "USER_NAME_EMPTY".into(),
                reason: "User name cannot be empty".to_string(),
                message: "User name cannot be empty".to_string(),
            },
        }
    }
    pub(in crate::common) fn user_name_too_short() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "USER_NAME_TOO_SHORT".into(),
                reason: "User name must be at least 3 characters".to_string(),
                message: "User name must be at least 3 characters".to_string(),
            },
        }
    }
    pub(in crate::common) fn user_name_too_long() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "USER_NAME_TOO_LONG".into(),
                reason: "User name must be at most 20 characters".to_string(),
                message: "User name must be at most 20 characters".to_string(),
            },
        }
    }
    pub(in crate::common) fn user_name_contains_illegal_character() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "USER_NAME_ILLEGAL".into(),
                reason: "user name contains illegal character".to_string(),
                message: "user name contains illegal character".to_string(),
            },
        }
    }
}
