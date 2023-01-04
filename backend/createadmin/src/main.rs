use base64::alphabet;
use base64::engine::fast_portable;
use base64::engine::fast_portable::FastPortable;
use mysql::params;
use mysql::prelude::Queryable;
use structopt::StructOpt;

// #[derive(Debug, StructOpt)]
// #[structopt(
//     name = "post-createadmin",
//     about = "CLI for creating admin for post backend services"
// )]
// enum Opt {
//     Mysql {
//         /// URI of post MySQL DB. Can be overriden by MYSQL_URL environment variable.
//         #[structopt(long)]
//         mysql_uri: Option<String>,
//         /// The password of admin
//         #[structopt(short, long)]
//         password: String,
//         /// Cost of bcrypt
//         #[structopt(long, default_value = "10")]
//         cost: u32,
//     },
//     MongoDb {
//         /// URI of post MongoDB. Can be overriden by MONGO_URL environment variable.
//         #[structopt(long)]
//         mongo_uri: Option<String>,
//         /// Databse name of the MongoDB.
//         #[structopt(long)]
//         mongo_db: Option<String>,
//         /// The password of admin
//         #[structopt(short, long)]
//         password: String,
//         /// Cost of bcrypt
//         #[structopt(long, default_value = "10")]
//         cost: u32,
//     },
// }

// const ID_ENGINE: FastPortable = FastPortable::from(&alphabet::URL_SAFE, fast_portable::NO_PAD);

// fn main() -> Result<(), Box<dyn std::error::Error>> {
//     let opt = Opt::from_args();
//     match opt {
//         Opt::Mysql {
//             mysql_uri,
//             password,
//             cost,
//         } => {
//             let mysql_uri = std::env::var("MYSQL_URI")
//                 .ok()
//                 .or(mysql_uri)
//                 .ok_or("cannot found MySQL URI")?;
//             let encrypted = bcrypt::hash(password, cost)?;
//             let pool = mysql::Pool::new_manual(1, 1, mysql::Opts::from_url(&mysql_uri)?)?;
//             let mut conn = pool.get_conn()?;

//             let mut id_bytes = [0u8; 8];
//             getrandom::getrandom(&mut id_bytes)?;

//             conn.exec_drop(
//                 "INSERT INTO admins (admin_id, encrypted_password) VALUES (:id, :encrypted_password)",
//                 params! {
//                     "id" => u64::from_le_bytes(id_bytes),
//                     "encrypted_password" => encrypted,
//                 },
//             )?;
//             println!("Admin Id: {}", base64::encode_engine(&id_bytes, &ID_ENGINE));
//             Ok(())
//         }
//     }
// }

#[derive(Debug, StructOpt)]
#[structopt(
    name = "post-createadmin",
    about = "CLI for creating admin for post backend services"
)]
struct Opt {
    /// URI of post MySQL DB. Can be overriden by MYSQL_URL environment variable.
    #[structopt(long)]
    mysql_uri: Option<String>,
    /// URI of post MongoDB.
    #[structopt(long)]
    mongodb_uri: Option<String>,
    /// The password of admin
    #[structopt(short, long)]
    password: String,
    /// Cost of bcrypt
    #[structopt(long, default_value = "10")]
    cost: u32,
}

const ID_ENGINE: FastPortable = FastPortable::from(&alphabet::URL_SAFE, fast_portable::NO_PAD);

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let opt = Opt::from_args();
    let encrypted = bcrypt::hash(opt.password, opt.cost)?;
    match (opt.mysql_uri, opt.mongodb_uri) {
        (Some(mysql_uri), None) => {
            let pool = mysql::Pool::new_manual(1, 1, mysql::Opts::from_url(&mysql_uri)?)?;
            let mut conn = pool.get_conn()?;

            let mut id_bytes = [0u8; 8];
            getrandom::getrandom(&mut id_bytes)?;

            conn.exec_drop(
                "INSERT INTO admins (admin_id, encrypted_password) VALUES (:id, :encrypted_password)",
                params! {
                    "id" => u64::from_le_bytes(id_bytes),
                    "encrypted_password" => encrypted,
                },
            )?;
            println!("Admin Id: {}", base64::encode_engine(&id_bytes, &ID_ENGINE));
            Ok(())
        },
        (None, Some(mongodb_uri)) => {
            let client = mongodb::sync::Client::with_uri_str(&mongodb_uri)?;
            let db = client.default_database()
                .ok_or_else(|| "No default databse")?;
            let collection = db.collection("admins");
            
            let inserted = collection.insert_one(
                mongodb::bson::doc! {
                    "encryptedPassword": encrypted,
                },
                None,
            )?;
            println!("Admin Id: {}", base64::encode_engine(
                &inserted.inserted_id.as_object_id()
                    .ok_or_else(|| "Inserted id is not object id")?
                    .bytes(), 
                &ID_ENGINE
            ));
            Ok(())
        },
        _ => Err("exact one of MySQL URI or MongoDB URI should be set".into()),
    }
}
