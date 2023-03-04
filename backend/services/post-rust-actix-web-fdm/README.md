# Post service implementation

The backend service of post using Rust, actix-web and sqlx.

This implementation uses [my development method](../../fdm.md).

Here is [the API document](../../api-document).

## Deployment

* Setup an MySQL database.
* Execute [setup.sql](../setup.sql) in the database.
* (Optional) use [create admin tool](../../createadmin) to create admin.
* Config by either setting up environment variables or using a `.env` file.
  * `MACHINE_ID`: integer between 0 and 1023. If there are several services running as a cluster, each two services should have different `MACHINE_ID`.
  * `MYSQL_URI`: MySQL URI, e.g. `mysql://username:password@localhost/database`.
  * `SECRET_KEY`: the base64 of the secret key to encrypt authorization token, e.g. `hPRYyVRiMyxpw5sBB1XeCMN1kFsDCqKvBi2QJxBVHQk=`
  * `TOKEN_VALID`: the duration the token validity, e.g. `30min`.
  * `RUST_LOG`: log level, e.g. `info`.
  * `LISTEN_ADDR`: listening address, e.g. `localhost:8432`.
* Run the application.

## Unit tests

* Because of ~~being too lazy~~ the time limitation, I only write a few unit tests as the example.
  * [Workflow unit-tests](src/delete_post/tests.rs).
  * [Model validation unit-tests](src/common/tests).
