pub mod api;
pub mod deps;

use crate::{common::*, define_error, define_steps};

pub struct Query {
    pub user_name: UserName,
    pub password: Password,
}

impl<'a> Steps<'a> {
    pub async fn workflow(self, input: Query) -> Result<UserId> {
        let (id, encrypted_password) = self.get_user_id_encrypted_password(input.user_name).await?;
        if input.password.verify(&encrypted_password)? {
            Ok(id)
        } else {
            Err(user_name_or_password_incorrect())
        }
    }
}

define_steps! {
    async fn get_user_id_encrypted_password(user_name: UserName) -> Result<(UserId, String)>;
}

define_error! {
    user_name_or_password_incorrect();
}
