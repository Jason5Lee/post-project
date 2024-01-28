use crate::common::api::*;
use crate::common::*;

use jsonwebtoken::{DecodingKey, EncodingKey};
use serde::{Deserialize, Serialize};

pub struct AuthConfig {
    pub valid_secs: u64,
    pub secret: Vec<u8>,
    pub admin_token: String,
}

#[derive(Clone, Debug, Serialize, Deserialize, PartialEq)]
#[allow(non_snake_case)]
pub struct Claim {
    pub exp: u64,
    pub userId: String,
}

impl super::Context {
    pub fn get_caller_identity(&self) -> Result<Option<Identity>> {
        match get_auth_token(self)? {
            AuthToken::Guest => Ok(None),
            AuthToken::User(user_token) => get_user_identity_from_token(&self.deps, user_token),
            AuthToken::Admin(admin_token) => {
                if admin_token == self.deps.auth.admin_token.as_bytes() {
                    Ok(Some(Identity::Admin))
                } else {
                    Err(invalid_auth())
                }
            }
        }
    }

    pub fn get_token_expire_time(&self) -> Time {
        let time_secs = std::time::SystemTime::now()
            .duration_since(std::time::UNIX_EPOCH)
            .unwrap()
            .as_secs();
        Time {
            // JWT exp precise to the second
            utc: (time_secs + self.deps.auth.valid_secs) * 1000,
        }
    }

    pub fn generate_user_token(&self, expire_time: Time, user: UserId) -> String {
        let exp = expire_time.utc / 1000;
        let claim = Claim {
            exp,
            userId: user.0,
        };
        jsonwebtoken::encode(
            &jsonwebtoken::Header::default(),
            &claim,
            &EncodingKey::from_secret(&self.deps.auth.secret),
        )
        .unwrap()
    }
}

fn decode_jwt(deps: &utils::Deps, token: &str) -> Result<Claim> {
    jsonwebtoken::decode::<Claim>(
        token,
        &DecodingKey::from_secret(&deps.auth.secret),
        &jsonwebtoken::Validation::default(),
    )
    .map(|data| data.claims)
    .map_err(|e| {
        log::info!("error: {e}");
        invalid_auth()
    })
}

fn decode_jwt_from_auth_header(deps: &utils::Deps, token: &[u8]) -> Result<Claim> {
    let token = std::str::from_utf8(token).map_err(|_| invalid_auth())?;
    decode_jwt(deps, token)
}

fn get_user_identity_from_token(deps: &utils::Deps, user_token: &[u8]) -> Result<Option<Identity>> {
    let claim = decode_jwt_from_auth_header(deps, user_token)?;
    Ok(Some(Identity::User(UserId(claim.userId))))
}
