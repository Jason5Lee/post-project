pub mod api;
pub mod deps;

use crate::{common::*, define_error, define_steps};

pub type Query = UserId;

pub struct UserInfoForPage {
    pub user_name: UserName,
    pub creation: Time,
}

define_steps! {
    async fn workflow(id: Query) -> Result<UserInfoForPage>;
}

define_error! {
    user_not_found();
}
