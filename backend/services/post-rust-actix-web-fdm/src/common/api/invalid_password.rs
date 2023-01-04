use crate::common::utils::error::*;

impl super::super::Password {
    pub(in crate::common) fn password_empty() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "PASSWORD_EMPTY".into(),
                reason: "password is empty".to_string(),
                message: "password is empty".to_string(),
            },
        }
    }
    pub(in crate::common) fn password_too_short() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "PASSWORD_TOO_SHORT".into(),
                reason: "password is too short".to_string(),
                message: "password is too short".to_string(),
            },
        }
    }
    pub(in crate::common) fn password_too_long() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "PASSWORD_TOO_LONG".into(),
                reason: "password is too long".to_string(),
                message: "password is too long".to_string(),
            },
        }
    }
}
