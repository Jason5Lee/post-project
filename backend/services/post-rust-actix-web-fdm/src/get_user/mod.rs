pub mod api;
pub mod deps;

use crate::{common::*, define_error, define_steps};

pub type Query = UserId;

pub struct User {
    pub user_name: UserName,
    pub creation: Time,
}

define_steps! {
    async fn workflow(id: Query) -> Result<User>;
}

define_error! {
    user_not_found();
}
