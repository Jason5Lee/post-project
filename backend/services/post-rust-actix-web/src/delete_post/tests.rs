use crate::common::*;

#[actix_rt::test]
async fn test_not_delete_by_not_creator() {
    struct Mock;

    #[async_trait(?Send)]
    impl super::MockSteps for Mock {
        async fn get_post_creator(&self, _: &PostId) -> Result<UserId> {
            Ok(UserId("1".into()))
        }
        async fn delete_post(&self, _: &PostId) -> Result<()> {
            panic!("post shouldn't be deleted by an user that is not the creator")
        }
    }

    assert_eq!(
        super::Steps(&Mock)
            .workflow(Identity::User(UserId("2".into())), PostId("1".into()))
            .await,
        Err(super::not_creator_admin())
    );
}

#[actix_rt::test]
async fn test_deleted_by_creator() {
    struct Mock;

    #[async_trait(?Send)]
    impl super::MockSteps for Mock {
        async fn get_post_creator(&self, _: &PostId) -> Result<UserId> {
            Ok(UserId("1".into()))
        }
        async fn delete_post(&self, _: &PostId) -> Result<()> {
            Ok(())
        }
    }

    assert_eq!(
        super::Steps(&Mock)
            .workflow(Identity::User(UserId("1".into())), PostId("1".into()))
            .await,
        Ok(())
    );
}

#[actix_rt::test]
async fn test_deleted_by_admin() {
    struct Mock;

    #[async_trait(?Send)]
    impl super::MockSteps for Mock {
        async fn get_post_creator(&self, _: &PostId) -> Result<UserId> {
            Ok(UserId("1".into()))
        }
        async fn delete_post(&self, _: &PostId) -> Result<()> {
            Ok(())
        }
    }

    assert_eq!(
        super::Steps(&Mock)
            .workflow(Identity::Admin, PostId("1".into()))
            .await,
        Ok(()),
    );
}
