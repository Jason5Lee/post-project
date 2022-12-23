pub mod api;
pub mod deps;

use crate::{common::*, define_error, define_steps};

pub enum Condition {
    No,
    Before(Time),
    After(Time),
}
pub struct Query {
    creator: Option<UserId>,
    condition: Condition,
    size: Size,
}

pub struct PostInfo {
    pub id: PostId,
    pub title: Title,
    pub creator: CreatorInfo,
    pub creation: Time,
}

pub struct CreatorInfo {
    pub id: UserId,
    pub name: UserName,
}

pub struct Output {
    pub posts: Vec<PostInfo>,
}

define_steps! {
    async fn workflow(input: Query) -> Result<Output>;
}

define_error! {
    creator_not_found();
}
