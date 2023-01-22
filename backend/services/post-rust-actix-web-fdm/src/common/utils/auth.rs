use crate::common::api::*;
use crate::common::*;

use jsonwebtoken::{DecodingKey, EncodingKey};
use serde::{Deserialize, Serialize};

pub struct AuthConfig {
    pub valid_secs: u64,
    pub secret: Vec<u8>,
}

#[derive(Clone, Debug, Serialize, Deserialize, PartialEq)]
#[allow(non_snake_case)]
pub struct Claim {
    pub exp: u64,
    pub userId: Option<String>,
    pub adminId: Option<String>,
}

impl super::Context {
    pub fn get_caller_identity(&self) -> Result<Option<Identity>> {
        match get_claim(self)? {
            None => Ok(None),
            Some(claim) => {
                if let Some(user_id) = claim.userId {
                    Ok(Some(Identity::User(UserId(user_id))))
                } else if let Some(admin_id) = claim.adminId {
                    Ok(Some(Identity::Admin(AdminId(admin_id))))
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

    pub fn generate_token(&self, expire_time: Time, identity: Identity) -> String {
        let exp = expire_time.utc / 1000;
        let claim = match identity {
            Identity::User(user_id) => Claim {
                exp,
                userId: Some(user_id.0),
                adminId: None,
            },
            Identity::Admin(admin_id) => Claim {
                exp,
                userId: None,
                adminId: Some(admin_id.0),
            },
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

fn get_claim(ctx: &utils::Context) -> Result<Option<Claim>> {
    match api::get_auth_token(ctx)? {
        None => Ok(None),
        Some(token) => decode_jwt_from_auth_header(&ctx.deps, token).map(Some),
    }
}
