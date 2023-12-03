pub mod api;
pub mod deps;

use crate::{common::*, define_error, define_steps};

pub struct Query {
    creator: Option<UserId>,
    page: Page,
    page_size: PageSize,
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
    pub total: u64,
    pub posts: Vec<Post>,
}

define_steps! {
    async fn workflow(input: Query) -> Result<Output>;
}

define_error! {
    creator_not_found();
}
