pub mod api;
pub mod deps;

use crate::{common::*, define_error, define_steps};

pub type Query = PostId;

pub struct CreatorInfo {
    pub name: UserName,
    pub id: UserId,
}
pub struct PostInfoForPage {
    pub creator: CreatorInfo,
    pub creation: Time,
    pub last_modified: Option<Time>,
    pub title: Title,
    pub content: PostContent,
}

define_steps! {
    async fn workflow(id: Query) -> Result<PostInfoForPage>;
}

define_error! {
    post_not_found();
}
