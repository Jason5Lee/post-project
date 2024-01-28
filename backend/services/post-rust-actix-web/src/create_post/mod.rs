pub mod api;
pub mod deps;

use crate::{common::*, define_error, define_steps};

pub struct Command {
    pub title: Title,
    pub content: PostContent,
}

define_steps! {
    async fn workflow(caller: UserId, input: Command) -> Result<PostId>;
}

define_error! {
    duplicate_title();
}
