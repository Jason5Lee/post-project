use std::rc::Rc;

use crate::common::*;
use crate::common::utils::{Endpoint, HttpMethod};
use actix_web::{HttpResponse, Result};
use apply::Apply;
use serde::Serialize;

use super::*;

pub const ENDPOINT: Endpoint = (HttpMethod::GET, "/identity");
pub async fn api(ctx: utils::Context) -> Result<HttpResponse> {
    let caller = ctx.get_caller_identity()?;
    let output = super::Steps::from_ctx(&ctx).workflow(caller).await?;
    HttpResponse::Ok()
        .json({
            #[derive(Serialize, Default)]
            pub struct ResponseDto {
                #[serde(skip_serializing_if = "Option::is_none")]
                user: Option<UserDto>,
                #[serde(skip_serializing_if = "std::ops::Not::not")] // Skip if false
                admin: bool,
            }

            #[derive(Serialize)]
            pub struct UserDto {
                pub name: Rc<str>,
                pub id: String,
            }

            match output {
                Some(IdentityInfo::User { id, name }) => ResponseDto {
                    user: Some(UserDto {
                        name: name.0,
                        id: id.0,
                    }),
                    ..Default::default()
                },
                Some(IdentityInfo::Admin) => ResponseDto {
                    admin: true,
                    ..Default::default()
                },
                None => Default::default(),
            }
        })
        .apply(Ok)
}
