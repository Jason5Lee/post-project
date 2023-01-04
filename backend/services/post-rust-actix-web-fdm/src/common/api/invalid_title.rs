use crate::common::utils::error::*;

impl super::super::Title {
    pub(in crate::common) fn title_empty() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "TITLE_EMPTY".into(),
                reason: "title is empty".to_string(),
                message: "title is empty".to_string(),
            },
        }
    }
    pub(in crate::common) fn title_too_short() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "TITLE_TOO_SHORT".into(),
                reason: "title is too short".to_string(),
                message: "title is too short".to_string(),
            },
        }
    }
    pub(in crate::common) fn title_too_long() -> ErrorBody {
        ErrorBody {
            error: ErrBody {
                error: "TITLE_TOO_LONG".into(),
                reason: "title is too long".to_string(),
                message: "title is too long".to_string(),
            },
        }
    }
}
