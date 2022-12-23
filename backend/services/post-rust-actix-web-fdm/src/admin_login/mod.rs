pub mod api;
pub mod deps;

use crate::{common::*, define_error, define_steps};

pub struct Query {
    id: AdminId,
    password: Password,
}

define_steps! {
    async fn workflow(input: Query) -> Result<AdminId>;
}

define_error! {
    id_or_password_incorrect();
}
