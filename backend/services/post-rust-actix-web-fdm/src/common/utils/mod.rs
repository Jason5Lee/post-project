use std::convert::{Infallible, TryInto};

use crate::common::api::invalid_id;
use actix_web::{dev::Payload, FromRequest, HttpRequest};
use futures_util::future::{ready, Ready};

use self::error::ErrorBody;
use super::{api::handle_internal_error, Result};
use base64::{
    alphabet,
    engine::fast_portable::{self, FastPortable},
};

pub mod auth;
pub mod error;
pub mod macros;

pub struct Context {
    pub request: HttpRequest,
    pub payload: Payload,
    pub deps: DataDeps,
}

impl Context {
    pub fn to<T: FromRequest>(&mut self) -> T::Future {
        T::from_request(&self.request, &mut self.payload)
    }

    pub fn discard_body(&mut self) {
        drop(self.payload.take())
    }

    pub fn extract<T: FromRequest>(&self) -> T::Future {
        T::extract(&self.request)
    }
}
impl<'a> FromRequest for Context {
    type Error = Infallible;
    type Future = Ready<Result<Context, Infallible>>;

    fn from_request(req: &HttpRequest, payload: &mut Payload) -> Self::Future {
        ready(Ok(Context {
            request: req.clone(),
            payload: payload.take(),
            deps: req
                .app_data::<DataDeps>()
                .expect("dependency not found")
                .clone(),
        }))
    }
}

pub struct Encryptor {
    pub cost: u32,
}
impl Encryptor {
    pub fn encrypt(&self, s: &str) -> Result<String> {
        bcrypt::hash(s, self.cost).map_err(handle_internal_error)
    }

    pub fn verify(plain: &str, encrypted: &str) -> Result<bool> {
        bcrypt::verify(plain, encrypted).map_err(handle_internal_error)
    }
}

pub struct Deps {
    pub pool: sqlx::MySqlPool,
    pub encryptor: Encryptor,
    pub id_gen: parking_lot::Mutex<snowflake::SnowflakeIdGenerator>,
    pub auth: auth::AuthConfig,
}

type DataDeps = actix_web::web::Data<Deps>;

const ID_ENGINE: FastPortable = FastPortable::from(&alphabet::URL_SAFE, fast_portable::NO_PAD);
pub fn format_id(id: u64) -> String {
    base64::encode_engine(id.to_le_bytes(), &ID_ENGINE)
}
pub fn parse_id(value: &str) -> Result<u64, (&str, ErrorBody)> {
    let bytes = base64::decode_engine(value, &ID_ENGINE)
        .map_err(|err| (value, invalid_id(err.to_string())))?;
    let bytes = (&bytes as &[u8])
        .try_into()
        .map_err(|_| (value, invalid_id("wrong length".into())))?;
    Ok(u64::from_le_bytes(bytes))
}

pub fn current_timestamp() -> u64 {
    std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .unwrap()
        .as_millis() as u64
}
