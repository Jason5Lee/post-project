use crate::common::{ErrBody, ErrorBody};

pub fn invalid_user_name() -> ErrorBody {
    ErrorBody {
        error: ErrBody {
            error: "INVALID_USER_NAME".into(),
            reason: "The user name is invalid".into(),
        },
    }
}

pub fn invalid_title() -> ErrorBody {
    ErrorBody {
        error: ErrBody {
            error: "INVALID_TITLE".into(),
            reason: "The title is invalid".into(),
        },
    }
}

pub fn invalid_text_post_content() -> ErrorBody {
    ErrorBody {
        error: ErrBody {
            error: "INVALID_TEXT_POST_CONTENT".into(),
            reason: "The content of the text post is invalid".into(),
        },
    }
}

pub fn invalid_url_post_content() -> ErrorBody {
    ErrorBody {
        error: ErrBody {
            error: "INVALID_URL_POST_CONTENT".into(),
            reason: "The content of the URL post is invalid".into(),
        },
    }
}

pub fn invalid_password() -> ErrorBody {
    ErrorBody {
        error: ErrBody {
            error: "INVALID_PASSWORD".into(),
            reason: "The password is invalid".into(),
        },
    }
}

pub fn invalid_time() -> ErrorBody {
    ErrorBody {
        error: ErrBody {
            error: "INVALID_TIMESTAMP".into(),
            reason: "The timestamp is invalid".into(),
        },
    }
}

pub fn invalid_page() -> ErrorBody {
    ErrorBody {
        error: ErrBody {
            error: "INVALID_PAGE".into(),
            reason: "The page number is invalid".into(),
        },
    }
}

pub fn invalid_page_size() -> ErrorBody {
    ErrorBody {
        error: ErrBody {
            error: "INVALID_PAGE_SIZE".into(),
            reason: "The page size is invalid".into(),
        },
    }
}
