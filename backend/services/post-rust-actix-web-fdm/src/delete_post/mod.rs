pub mod api;
pub mod deps;
#[cfg(test)]
pub mod tests;

use crate::{common::*, define_error, define_steps};

pub type Command = PostId;

impl<'a> Steps<'a> {
    pub async fn workflow(self, caller: Identity, input: Command) -> Result<()> {
        let auth: bool = match caller {
            Identity::Admin(_) => true,
            Identity::User(id) => id == self.get_post_creator(input).await?,
        };

        if auth {
            self.delete_post(input).await
        } else {
            Err(forbidden())
        }
    }
}

define_steps! {
    async fn get_post_creator(post: PostId) -> Result<UserId>;
    async fn delete_post(id: PostId) -> Result<()>;
}

define_error! {
    forbidden();
    post_not_found();
}