use actix_web::http::StatusCode;
use actix_web::HttpResponse;
use serde::Serialize;
use std::borrow::Cow;

#[derive(PartialEq, Debug)]
pub struct ErrorResponse {
    pub status_code: StatusCode,
    pub body: ErrorBody,
}

impl From<(StatusCode, ErrorBody)> for ErrorResponse {
    fn from((status_code, body): (StatusCode, ErrorBody)) -> Self {
        Self { status_code, body }
    }
}

#[derive(PartialEq, Debug, Serialize)]
pub struct ErrorBody {
    pub error: ErrBody,
}

#[derive(PartialEq, Debug, Serialize)]
pub struct ErrBody {
    pub error: Cow<'static, str>,
    pub reason: Cow<'static, str>,
}

impl std::fmt::Display for ErrorResponse {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        let err = &self.body.error;
        write!(f, "{}: {} ({})", self.status_code, err.error, err.reason)
    }
}

impl actix_web::ResponseError for ErrorResponse {
    fn status_code(&self) -> StatusCode {
        self.status_code
    }

    fn error_response(&self) -> HttpResponse<actix_web::body::BoxBody> {
        HttpResponse::build(self.status_code())
            .json(&self.body)
            .map_into_boxed_body()
    }
}
