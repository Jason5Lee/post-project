use std::fmt::{Debug, Formatter};
use std::rc::Rc;
use crate::common::utils::error::*;

pub mod api;
pub mod db;
pub mod utils;
#[cfg(test)]
mod tests;

pub type Result<T, E = ErrorResponse> = std::result::Result<T, E>;

#[derive(Debug, Ord, PartialOrd, Eq, PartialEq, Copy, Clone)]
pub struct Time {
    pub utc: u64, 
}

#[derive(Debug, PartialEq, Eq, Clone, Copy)]
pub enum Identity {
    User(UserId),
    Admin(AdminId),
}

#[derive(Debug, PartialEq, Eq, Clone, Copy)]
pub struct UserId(pub u64);

#[derive(Debug, PartialEq, Eq, Clone, Copy)]
pub struct AdminId(pub u64);

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct UserName(Rc<str>);

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct User {
    pub id: UserId,
    pub name: UserName,
    pub creation: Time,
}

#[derive(PartialEq, Eq, Clone)]
pub struct Password {
    plain: String,
}

#[derive(Debug, PartialEq, Eq, Clone, Copy)]
pub struct LengthLimit(u32);

#[derive(Clone, Copy, PartialEq, Eq)]
pub struct PostId(pub u64);

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct Title(String);

#[derive(Debug, PartialEq, Eq, Clone)]
pub enum PostContent {
    Post(String),
    Url(url::Url),
}

#[derive(Debug, PartialEq, Eq, Clone, Copy)]
pub struct Size(u32);

impl UserName {
    fn is_legal_character(ch: char) -> bool {
        ch.is_ascii_alphanumeric() || ch == '_' || ch == '-'
    }

    pub fn try_new(value: String) -> Result<Self, (String, ErrorBody)> {
        if value.is_empty() {
            Err((value, Self::user_name_empty()))
        } else if value.len() < 3 {
            Err((value, Self::user_name_too_short()))
        } else if value.len() > 20 {
            Err((value, Self::user_name_too_long()))
        } else if !value.chars().all(Self::is_legal_character) {
            Err((value, Self::user_name_contains_illegal_character()))
        } else {
            Ok(Self(value.into()))
        }
    }
    
    pub fn as_str(&self) -> &str {
        &self.0
    }
    pub fn into_rc_str(self) -> Rc<str> {
        self.0
    }
}

impl Title {
    pub fn try_new(value: String) -> Result<Self, (String, ErrorBody)>  {
        if value.is_empty() {
            Err((value, Self::title_empty()))
        } else if value.len() < 3 {
            Err((value, Self::title_too_short()))
        } else if value.len() > 171 {
            Err((value, Self::title_too_long()))
        } else {
            Ok(Self(value))
        }
    }
    
    pub fn as_str(&self) -> &str {
        &self.0
    }
    pub fn into_string(self) -> String {
        self.0
    }
}

impl PostContent {
    pub fn try_new_url(value: String) -> Result<Self, (String, ErrorBody)> {
        if value.is_empty() {
            Err((value, Self::url_empty()))
        } else if value.len() > 65535 {
            Err((value, Self::url_too_long()))
        } else {
            match url::Url::parse(&value) {
                Ok(url) => Ok(Self::Url(url)),
                Err(err) => Err((value, Self::url_invalid(err))),
            }
        }
    }

    pub fn try_new_post(value: String) -> Result<Self, (String, ErrorBody)> {
        if value.is_empty() {
            Err((value, Self::post_empty()))
        } else if value.len() > 65535 {
            Err((value, Self::post_too_long()))
        } else {
            Ok(Self::Post(value))
        }
    }
}

impl Time {
    pub fn now() -> Time {
        Time {
            utc: utils::current_timestamp(),
        }
    }
}

impl Debug for Password {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(f, "Password {{ plain: <hidden> }}")
    }
}
impl Password {
    pub fn try_from_plain(value: String) -> Result<Self, (String, ErrorBody)> {
        if value.is_empty() {
            Err((value, Self::password_empty()))
        } else if value.len() < 5 {
            Err((value, Self::password_too_short()))
        } else if value.len() > 72 {
            Err((value, Self::password_too_long()))
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

const DEFAULT_SIZE: u32 = 20;
const MAX_SIZE: u32 = 500;

impl Size {
    pub fn try_new(s: Option<u32>) -> Result<Self, (u32, ErrorBody)> {
        match s {
            None => Ok(Self(DEFAULT_SIZE)),
            Some(size) => {
                if size == 0 {
                    Err((size, Self::size_non_positive()))
                } else if size > MAX_SIZE {
                    Ok(Self(MAX_SIZE))
                } else {
                    Ok(Self(size))
                }
            }
        }
    }
    
    pub fn to_u32(self) -> u32 {
        self.0
    }
}
