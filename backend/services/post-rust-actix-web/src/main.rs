use actix_web::{middleware::Logger, App, HttpServer};
use post_rust_actix_web::common::utils::auth::AuthConfig;
use post_rust_actix_web::common::utils::Encryptor;
use serde::Deserialize;
use sqlx::mysql::MySqlPoolOptions;

#[actix_web::main]
async fn main() {
    dotenv::dotenv().ok();

    #[derive(Deserialize)]
    struct Config {
        listen_addr: String,
        admin_token: String,
        mysql_max_connections: Option<u32>,
        mysql_uri: String,
        secret_key: String,
        token_valid_duration: String,
        cost: Option<u32>,
        // rust_log is read by env_logger
    }

    let config: Config = envy::from_env().expect("unable to read config");

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
        .token_valid_duration
        .parse::<humantime::Duration>()
        .expect("invalid token_valid")
        .as_secs();

    let cost = config.cost.unwrap_or(bcrypt::DEFAULT_COST);

    let deps = actix_web::web::Data::new(post_rust_actix_web::common::utils::Deps {
        pool,
        encryptor: Encryptor { cost },
        auth: AuthConfig {
            valid_secs,
            secret,
            admin_token: config.admin_token,
        },
    });

    env_logger::init();
    let server = HttpServer::new(move || {
        macro_rules! register_service {
            ($app:ident, $api_name:ident) => {
                $app = $app.route(
                    post_rust_actix_web::$api_name::api::ENDPOINT.1,
                    actix_web::web::method(post_rust_actix_web::$api_name::api::ENDPOINT.0)
                        .to(post_rust_actix_web::$api_name::api::api),
                )
            };
        }
        let mut app = App::new().wrap(Logger::default()).app_data(deps.clone());
        register_service!(app, create_post);
        register_service!(app, user_login);
        register_service!(app, list_posts);
        register_service!(app, user_register);
        register_service!(app, get_post);
        register_service!(app, delete_post);
        register_service!(app, edit_post);
        register_service!(app, get_identity);
        register_service!(app, get_user);
        app.default_service(
            actix_web::web::route().to(post_rust_actix_web::common::api::api_not_found),
        )
    })
    .bind(&config.listen_addr)
    .expect("unable to bind")
    .run();
    log::info!("Listening {}", config.listen_addr);
    server.await.expect("error while running the server");
}
