use super::*;
use crate::common::*;
use crate::common::api::*;
use crate::common::utils::error::*;
use actix_web::http::StatusCode;
use actix_web::{
    post, web::Json as BodyJson, web::Path as UrlPath, HttpResponse,
};
use serde::Deserialize;

#[post("/post/{id}")]
pub async fn api(mut ctx: utils::Context) -> Result<HttpResponse> {
    let caller = ctx.auth_user_only()?;
    #[derive(Deserialize)]
    #[allow(non_snake_case)]
    pub struct RequestDto {
        pub post: Option<String>,
        pub url: Option<String>,
    }

    let req = ctx.to::<BodyJson<RequestDto>>().await.map_err(bad_request)?.0;
    let input = Command {
        id: PostId(
            utils::parse_id(&ctx.to::<UrlPath<(String,)>>().await.map_err(bad_request)?.to_owned().0)
                .map_err(|_| post_not_found())?,
        ),
        new_content: match (req.post, req.url) {
            (Some(post), None) => PostContent::try_new_post(post).map_err(as_unprocessable_entity)?,
            (None, Some(url)) => {
                PostContent::try_new_url(url).map_err(as_unprocessable_entity)?
            }
            _ => return Err((StatusCode::UNPROCESSABLE_ENTITY, ErrorBody {
                error: ErrBody {
                    error: "ONLY_EXACT_ONE_OF_POST_URL".into(),
                    reason: "only exact one of the post and the url field should exist".to_string(),
                    message: CLIENT_BUG_MESSAGE.to_string(),
                }
            }).into()),
        },
    };
    super::Steps::from_ctx(&ctx).workflow(caller, input).await?;
    Ok(HttpResponse::NoContent().finish())
}

pub fn post_not_found() -> ErrorResponse {
    (StatusCode::NOT_FOUND, ErrorBody {
        error: ErrBody {
            error: "POST_NOT_FOUND".into(),
            reason: "post not found".to_string(),
            message: "the post does not exist".to_string()
        }
    }).into()
}

pub fn not_creator() -> ErrorResponse {
    (StatusCode::FORBIDDEN, ErrorBody {
        error: ErrBody {
            error: "NOT_CREATOR".into(),
            reason: "the user is not the creator of the post".to_string(),
            message: "you have no permission to edit this post".to_string()
        }
    }).into()
}

pub fn type_diff() -> ErrorResponse {
    (StatusCode::UNPROCESSABLE_ENTITY, ErrorBody {
        error: ErrBody {
            error: "TYPE_DIFF".into(),
            reason: "the type of the post is different from the request".to_string(),
            message: "you cannot change the post type".to_string()
        }
    }).into()
}
