# Post service implementation

The backend service of post using Rust, actix-web and sqlx.

This implementation uses [my development method](../../mdm.md).

Here is [the API document](../../api-document).

## Deployment

* Setup an MySQL database.
* Execute [setup.sql](./db/setup.sql) in the database.
* Config by either setting up environment variables or using a `.env` file.
  * `LISTEN_ADDR`: listening address, e.g. `localhost:8432`.
  * `MYSQL_URI`: MySQL URI, e.g. `mysql://username:password@localhost/database`.
  * `MYSQL_MAX_CONNECTIONS`: (optional) MySQL maximum number of connections.
  * `ADMIN_TOKEN`: the value of the admin token.
  * `SECRET_KEY`: the base64 of the secret key to encrypt authorization token, e.g. `hPRYyVRiMyxpw5sBB1XeCMN1kFsDCqKvBi2QJxBVHQk=`
  * `TOKEN_VALID_DURATION`: the duration the token validity, e.g. `30min`.
  * `RUST_LOG`: (optional) log level, e.g. `info`.
  * `COST`: (optional) the cost of the password encryption.
* Run the application.

## Unit tests

* Because of ~~being too lazy~~ the time limitation, I only write a few unit tests as the example.
  * [Workflow unit-tests](src/delete_post/tests.rs).
  * [Model validation unit-tests](src/common/tests).
