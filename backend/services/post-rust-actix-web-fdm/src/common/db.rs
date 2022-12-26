use std::fmt;
use std::fmt::Formatter;

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

pub const POSTS: Table = Table("posts");
pub mod posts {
    use super::*;

    pub const POST_ID: UniqueColumn = Column::primary("post_id");
    pub const CREATOR: Column = Column::name("creator");
    pub const CREATION_TIME: Column = Column::name("creation_time");
    pub const LAST_MODIFIED: Column = Column::name("last_modified");
    pub const TITLE: UniqueColumn = Column::unique("title", "UC_title");
    pub const TEXT: Column = Column::name("text_content");
    pub const URL: Column = Column::name("url_link");
}
pub const USERS: Table = Table("users");
pub mod users {
    use super::*;
    pub const USER_ID: UniqueColumn = Column::primary("user_id");
    pub const USER_NAME: UniqueColumn = Column::unique("user_name", "idx_user_name");
    pub const ENCRYPTED_PASSWORD: Column = Column::name("encrypted_password");
    pub const CREATION_TIME: Column = Column::name("creation_time");
}
pub const ADMIN: Table = Table("admins");
pub mod admin {
    use super::*;
    pub const ADMIN_ID: UniqueColumn = Column::primary("admin_id");
    pub const ENCRYPTED_PASSWORD: Column = Column::name("encrypted_password");
}

pub fn is_unique_violation_in(err: &sqlx::Error, column: UniqueColumn) -> bool {
    if let Some(err) = err.as_database_error() {
        err.code().map_or(false, |code| code == "23000")
            && (err.message().ends_with(&iformat!("'" column.unique_constraint "'")) || // MariaDB
                err.message().ends_with(&iformat!("." column.unique_constraint "'")))
    // MySQL
    } else {
        false
    }
}
