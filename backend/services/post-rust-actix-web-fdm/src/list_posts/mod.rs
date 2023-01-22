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

pub struct Post {
    pub id: PostId,
    pub title: Title,
    pub creator: Creator,
    pub creation: Time,
}

pub struct Creator {
    pub id: UserId,
    pub name: UserName,
}

pub struct Output {
    pub posts: Vec<Post>,
}

define_steps! {
    async fn workflow(input: Query) -> Result<Output>;
}

define_error! {
    creator_not_found();
}
