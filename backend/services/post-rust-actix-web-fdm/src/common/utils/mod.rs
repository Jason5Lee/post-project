use std::convert::Infallible;

use actix_web::{dev::Payload, FromRequest, HttpRequest};
use futures_util::future::{ready, Ready};

use super::{api::handle_internal_error, Result};

pub mod auth;
pub mod error;
pub mod macros;

pub struct Context {
    pub request: HttpRequest,
    pub payload: Payload,
    pub deps: DataDeps,
}

impl Context {
    pub fn get<T: FromRequest>(&mut self) -> T::Future {
        T::from_request(&self.request, &mut self.payload)
    }

    pub fn discard_body(&mut self) {
        drop(self.payload.take())
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

pub fn current_timestamp() -> u64 {
    std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)
        .unwrap()
        .as_millis() as u64
}
