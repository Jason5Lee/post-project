use crate::common::utils::error::*;
use std::fmt::{Debug, Formatter};
use std::rc::Rc;
use url::Url;

pub mod api;
pub mod db;
#[cfg(test)]
mod tests;
pub mod utils;

pub type Result<T, E = ErrorResponse> = std::result::Result<T, E>;

#[derive(Debug, Ord, PartialOrd, Eq, PartialEq, Copy, Clone)]
pub struct Time {
    pub utc: u64,
}

#[derive(Debug, PartialEq, Eq, Clone)]
pub enum Identity {
    User(UserId),
    Admin,
}

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct UserId(pub String);

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct UserName(pub Rc<str>);

#[derive(PartialEq, Eq, Clone)]
pub struct Password {
    plain: String,
}

#[derive(Debug, PartialEq, Eq, Clone, Copy)]
pub struct LengthLimit(pub u32);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct PostId(pub String);

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct Title(pub String);

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct TextPostContent(pub String);

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct UrlPostContent(pub String);

#[derive(Debug, PartialEq, Eq, Clone)]
pub enum PostContent {
    Text(TextPostContent),
    Url(UrlPostContent),
}

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct Page(pub u64);

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct PageSize(pub u64);

impl UserName {
    fn is_legal_character(ch: char) -> bool {
        ch.is_ascii_alphanumeric() || ch == '_' || ch == '-'
    }

    pub fn try_new(value: String) -> Result<Self, ErrorBody> {
        if value.is_empty() {
            Err(Self::user_name_empty())
        } else if value.len() < 3 {
            Err(Self::user_name_too_short())
        } else if value.len() > 20 {
            Err(Self::user_name_too_long())
        } else if !value.chars().all(Self::is_legal_character) {
            Err(Self::user_name_contains_illegal_character())
        } else {
            Ok(Self(value.into()))
        }
    }
}

impl Title {
    pub fn try_new(value: String) -> Result<Self, ErrorBody> {
        if value.is_empty() {
            Err(Self::title_empty())
        } else if value.len() < 3 {
            Err(Self::title_too_short())
        } else if value.len() > 171 {
            Err(Self::title_too_long())
        } else {
            Ok(Self(value))
        }
    }
}

impl TextPostContent {
    pub fn try_new(value: String) -> Result<Self, ErrorBody> {
        if value.is_empty() {
            Err(Self::text_post_content_empty())
        } else if value.len() > 65535 {
            Err(Self::text_post_content_too_long())
        } else {
            Ok(Self(value))
        }
    }
}

impl UrlPostContent {
    pub fn try_new(value: String) -> Result<Self, ErrorBody> {
        if value.is_empty() {
            Err(Self::url_post_content_empty())
        } else if value.len() > 65535 {
            Err(Self::url_post_content_too_long())
        } else {
            match Url::parse(&value) {
                Ok(_) => Ok(Self(value)),
                Err(err) => Err(Self::url_post_content_invalid(err)),
            }
        }
    }
}

impl Debug for Password {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(f, "Password {{ plain: <hidden> }}")
    }
}
impl Password {
    pub fn try_from_plain(value: String) -> Result<Self, ErrorBody> {
        if value.is_empty() {
            Err(Self::password_empty())
        } else if value.len() < 5 {
            Err(Self::password_too_short())
        } else if value.len() > 72 {
            // Limitation of the bcrypt algorithm.
            Err(Self::password_too_long())
        } else {
            Ok(Self { plain: value })
        }
    }

    pub fn to_encrypted(&self, encryptor: &utils::Encryptor) -> Result<String> {
        encryptor.encrypt(&self.plain)
    }

    pub fn verify(&self, encrypted: &str) -> Result<bool> {
        utils::Encryptor::verify(&self.plain, encrypted)
    }
}

impl Page {
    pub fn try_new(page: u64) -> Result<Page, ErrorBody> {
        if page == 0 {
            Err(Self::invalid_page())
        } else {
            Ok(Page(page))
        }
    }
}

impl PageSize {
    pub fn try_new(page_size: u64) -> Result<PageSize, ErrorBody> {
        if page_size == 0 {
            Err(Self::invalid_page_size())
        } else {
            Ok(PageSize(page_size))
        }
    }
}
