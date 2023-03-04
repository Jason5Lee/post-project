use std::convert::TryInto;
use std::fmt;
use std::fmt::Formatter;

use base64::alphabet;
use base64::engine::fast_portable::{self, FastPortable};

#[derive(Copy, Clone, Eq, PartialEq)]
pub struct Table(pub &'static str);
impl From<&'static str> for Table {
    fn from(name: &'static str) -> Self {
        Self(name)
    }
}
impl fmt::Display for Table {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.0)
    }
}

#[derive(Copy, Clone, Eq, PartialEq)]
pub struct Column {
    pub name: &'static str,
}

impl fmt::Display for Column {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.name)
    }
}

#[derive(Copy, Clone, Eq, PartialEq)]
pub struct UniqueColumn {
    pub name: &'static str,
    unique_constraint: &'static str,
}

impl fmt::Display for UniqueColumn {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.name)
    }
}
impl From<UniqueColumn> for Column {
    fn from(u: UniqueColumn) -> Self {
        Self { name: u.name }
    }
}

impl Column {
    pub const fn name(name: &'static str) -> Self {
        Column { name }
    }
    pub const fn primary(name: &'static str) -> UniqueColumn {
        UniqueColumn {
            name,
            unique_constraint: "PRIMARY",
        }
    }
    pub const fn unique(name: &'static str, unique_constraint: &'static str) -> UniqueColumn {
        UniqueColumn {
            name,
            unique_constraint,
        }
    }
}

pub const POST: Table = Table("post");
pub const POST_POST_ID: UniqueColumn = Column::primary("post_id");
pub const POST_CREATOR: Column = Column::name("creator");
pub const POST_CREATION_TIME: Column = Column::name("creation_time");
pub const POST_LAST_MODIFIED: Column = Column::name("last_modified");
pub const POST_TITLE: UniqueColumn = Column::unique("title", "UC_title");
pub const POST_TEXT: Column = Column::name("text_content");
pub const POST_URL: Column = Column::name("url_link");

pub const USER: Table = Table("users"); // `user` is MySQL reserved word
pub const USER_USER_ID: UniqueColumn = Column::primary("user_id");
pub const USER_USER_NAME: UniqueColumn = Column::unique("user_name", "idx_user_name");
pub const USER_ENCRYPTED_PASSWORD: Column = Column::name("encrypted_password");
pub const USER_CREATION_TIME: Column = Column::name("creation_time");

pub const ADMIN: Table = Table("admins"); // `admin` is MySQL reserved word
pub const ADMIN_ADMIN_ID: UniqueColumn = Column::primary("admin_id");
pub const ADMIN_ENCRYPTED_PASSWORD: Column = Column::name("encrypted_password");

pub enum UniqueViolationError {
    PrimaryKey,
    OtherColumn,
}
pub fn analysis_unique_violation_error(err: &sqlx::Error) -> Option<UniqueViolationError> {
    if let Some(err) = err.as_database_error() {
        if err.code().map_or(false, |code| code == "23000") {
            return if err.message().ends_with("'PRIMARY'") || // MariaDB
                /* MySQL */ err.message().ends_with(".PRIMARY'")
            {
                Some(UniqueViolationError::PrimaryKey)
            } else {
                Some(UniqueViolationError::OtherColumn)
            };
        }
    }
    None
}

const ID_ENGINE: FastPortable = FastPortable::from(&alphabet::URL_SAFE, fast_portable::NO_PAD);
pub fn format_id(id: u64) -> String {
    base64::encode_engine(id.to_le_bytes(), &ID_ENGINE)
}
pub fn parse_id(value: &str) -> Option<u64> {
    let bytes = base64::decode_engine(value, &ID_ENGINE).ok()?;
    let bytes = (&bytes as &[u8]).try_into().ok()?;
    Some(u64::from_le_bytes(bytes))
}
