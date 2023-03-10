pub mod api;
pub mod deps;

use crate::common::*;
use crate::{define_error, define_steps};

pub struct Command {
    pub user_name: UserName,
    pub password: Password,
}

impl<'a> Steps<'a> {
    pub async fn workflow(self, input: Command) -> Result<UserId> {
        let Command {
            user_name,
            password,
        } = input;
        self.insert_user(user_name, password).await
    }
}

define_steps! {
    async fn insert_user(user_name: UserName, password: Password) -> Result<UserId>;
}

define_error! {
    user_name_already_exists();
}
