use actix_web::{middleware::Logger, App, HttpServer};
use serde::Deserialize;
use sqlx::mysql::MySqlPoolOptions;
use post_rust_actix_web_fdm::common::utils::auth::AuthConfig;
use post_rust_actix_web_fdm::common::utils::Encryptor;

#[actix_web::main]
async fn main() {
    dotenv::dotenv().ok();

    #[derive(Deserialize)]
    struct Config {
        listen_addr: String,
        machine_id: i32,
        node_id: i32,
        mysql_max_connections: Option<u32>,
        mysql_uri: String,
        secret_key: String,
        token_valid: String,
        cost: Option<u32>,
    }

    let config: Config = envy::from_env().expect("unable to read config");

    let id_gen = parking_lot::Mutex::new(snowflake::SnowflakeIdGenerator::new(
        config.machine_id,
        config.node_id,
    ));
    let mut pool = MySqlPoolOptions::new();

    if let Some(mc) = config.mysql_max_connections {
        pool = pool.max_connections(mc)
    }

    let pool = pool
        .connect(&config.mysql_uri)
        .await
        .expect("unable to connect to the MySQL");

    let secret = base64::decode(&config.secret_key).expect("invalid secret key");
    let valid_secs = config
        .token_valid
        .parse::<humantime::Duration>()
        .expect("invalid token_valid")
        .as_secs();

    let cost = config.cost.unwrap_or(bcrypt::DEFAULT_COST);

    let deps = actix_web::web::Data::new(post_rust_actix_web_fdm::common::utils::Deps {
        id_gen,
        pool,
        encryptor: Encryptor { cost },
        auth: AuthConfig {
            valid_secs,
            secret,
        },
    });

    env_logger::init();
    let server = HttpServer::new(move || {
        App::new()
            .wrap(Logger::default())
            .app_data(deps.clone())
            .service(post_rust_actix_web_fdm::create_post::api::api)
            .service(post_rust_actix_web_fdm::user_login::api::api)
            .service(post_rust_actix_web_fdm::list_posts::api::api)
            .service(post_rust_actix_web_fdm::user_register::api::api)
            .service(post_rust_actix_web_fdm::get_post::api::api)
            .service(post_rust_actix_web_fdm::delete_post::api::api)
            .service(post_rust_actix_web_fdm::edit_post::api::api)
            .service(post_rust_actix_web_fdm::admin_login::api::api)
            .service(post_rust_actix_web_fdm::get_identity::api::api)
            .service(post_rust_actix_web_fdm::get_user::api::api)
    })
    .bind(&config.listen_addr)
.expect("unable to bind")
    .run();
    log::info!("Listening {}", config.listen_addr);
    server
        .await
        .expect("error while running the server");
}
