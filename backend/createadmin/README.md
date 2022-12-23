# Createadmin

Tool to create admin for backend service.

For safety reason, creating admin is not part of the service API.

## Usage

```
USAGE:
    post-createadmin mysql [OPTIONS] --password <password>

FLAGS:
    -h, --help       Prints help information
    -V, --version    Prints version information

OPTIONS:
        --cost <cost>              Cost of bcrypt [default: 10]
        --mysql-uri <mysql-uri>    URI of rustpost MySQL DB. Can be overriden by MYSQL_URL environment variable
    -p, --password <password>      The password of admin
```
