use std::convert::TryInto;

use base64::alphabet;
use base64::engine::fast_portable::{self, FastPortable};

pub const POST: &str = "post";
pub const POST_POST_ID: &str = "post_id";
pub const POST_CREATOR: &str = "creator";
pub const POST_CREATION_TIME: &str = "creation_time";
pub const POST_LAST_MODIFIED: &str = "last_modified";
pub const POST_TITLE: &str = "title";
pub const POST_TEXT: &str = "text_content";
pub const POST_URL: &str = "url_link";

pub const USER: &str = "users"; // `user` is MySQL reserved word
pub const USER_USER_ID: &str = "user_id";
pub const USER_USER_NAME: &str = "user_name";
pub const USER_ENCRYPTED_PASSWORD: &str = "encrypted_password";
pub const USER_CREATION_TIME: &str = "creation_time";

pub fn is_unique_violation_error(err: &sqlx::Error) -> bool {
    err.as_database_error().map_or(false, |err| {
        err.code().map_or(false, |code| code == "23000")
    })
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
