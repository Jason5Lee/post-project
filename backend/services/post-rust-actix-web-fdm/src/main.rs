use actix_web::{middleware::Logger, App, HttpServer};
use serde::Deserialize;
use sqlx::mysql::MySqlPoolOptions;
use rustpost_api::common::utils::auth::AuthConfig;
use rustpost_api::common::utils::Encryptor;

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

    let deps = actix_web::web::Data::new(rustpost_api::common::utils::Deps {
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
            .service(rustpost_api::create_post::api::api)
            .service(rustpost_api::user_login::api::api)
            .service(rustpost_api::list_posts::api::api)
            .service(rustpost_api::user_register::api::api)
            .service(rustpost_api::get_post::api::api)
            .service(rustpost_api::delete_post::api::api)
            .service(rustpost_api::edit_post::api::api)
            .service(rustpost_api::admin_login::api::api)
            .service(rustpost_api::get_identity::api::api)
            .service(rustpost_api::get_user::api::api)
    })
    .bind(&config.listen_addr)
.expect("unable to bind")
    .run();
    log::info!("Listening {}", config.listen_addr);
    server
        .await
        .expect("error while running the server");
}
