use base64::alphabet;
use base64::engine::fast_portable;
use base64::engine::fast_portable::FastPortable;
use mysql::params;
use mysql::prelude::Queryable;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
#[structopt(
    name = "post-createadmin",
    about = "CLI for creating admin for post backend services"
)]
enum Opt {
    Mysql {
        /// URI of rustpost MySQL DB. Can be overriden by MYSQL_URL environment variable.
        #[structopt(long)]
        mysql_uri: Option<String>,
        /// The password of admin
        #[structopt(short, long)]
        password: String,
        /// Cost of bcrypt
        #[structopt(long, default_value = "10")]
        cost: u32,
    },
}

const ID_ENGINE: FastPortable = FastPortable::from(&alphabet::URL_SAFE, fast_portable::NO_PAD);

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let opt = Opt::from_args();
    match opt {
        Opt::Mysql {
            mysql_uri,
            password,
            cost,
        } => {
            let mysql_uri = std::env::var("MYSQL_URI")
                .ok()
                .or(mysql_uri)
                .ok_or("cannot found MySQL URI")?;
            let encrypted = bcrypt::hash(password, cost)?;
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
        }
    }
}
