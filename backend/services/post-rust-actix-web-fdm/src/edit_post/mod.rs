pub mod api;
pub mod deps;

use crate::{common::*, define_error, define_steps};

pub struct Command {
    pub id: PostId,
    pub new_content: PostContent,
}

impl<'a> Steps<'a> {
    pub async fn workflow(self, caller: UserId, input: Command) -> Result<()> {
        self.checks_user_is_creator_and_content_has_the_same_post_type(
            &input.id,
            &caller,
            &input.new_content,
        )
        .await?;
        self.update_post(&input.id, input.new_content).await
    }
}

define_steps! {
    async fn checks_user_is_creator_and_content_has_the_same_post_type(post: &PostId, user: &UserId, content: &PostContent) -> Result<()>;
    async fn update_post(post: &PostId, new_content: PostContent) -> Result<()>;
}

define_error! {
    post_not_found();
    not_creator();
    type_diff();
}
