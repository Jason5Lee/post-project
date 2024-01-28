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

    pub fn try_new(value: String) -> Option<Self> {
        if value.len() >= 3 && value.len() <= 20 && value.chars().all(Self::is_legal_character) {
            Some(Self(value.into()))
        } else {
            None
        }
    }
}

impl Title {
    pub fn try_new(value: String) -> Option<Self> {
        if value.len() >= 3 && value.len() <= 171 {
            Some(Self(value.into()))
        } else {
            None
        }
    }
}

impl TextPostContent {
    pub fn try_new(value: String) -> Option<Self> {
        if value.len() <= 65535 {
            Some(Self(value.into()))
        } else {
            None
        }
    }
}

impl UrlPostContent {
    pub fn try_new(value: String) -> Option<Self> {
        if value.len() > 65535 {
            return None;
        }
        match Url::parse(&value) {
            Ok(_) => Some(Self(value)),
            Err(_) => None,
        }
    }
}

impl Debug for Password {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(f, "Password {{ plain: <hidden> }}")
    }
}
impl Password {
    pub fn try_from_plain(value: String) -> Option<Self> {
        if value.len() >= 5 && value.len() <= 72 {
            // Limitation of the bcrypt algorithm.
            Some(Self { plain: value })
        } else {
            None
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
    pub fn try_new(page: u64) -> Option<Self> {
        if page == 0 {
            None
        } else {
            Some(Page(page))
        }
    }
}

impl PageSize {
    pub fn try_new(page_size: u64) -> Option<Self> {
        if page_size > 0 && page_size <= 50 {
            Some(PageSize(page_size))
        } else {
            None
        }
    }
}
