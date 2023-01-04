# Post service implementation

The backend service of post using NodeJS, TypeScript, Koa and MongoDB.

This implementation uses [my development method](../../fdm.md).

Here is [the API document](../../api-document).

## Deployment

* Setup a MongoDB.
* Run `yarn install`.
* (Optional) run `yarn run generate` to generate `src/addRoutes.ts`.
* (Optional) use [create admin tool](../../createadmin) to create admin.
* Config by either setting up environment variables or using a `.env` file.
  * `LISTEN_HOST`: listening host.
  * `LISTEN_PORT`: listening port.
  * `MONGO_URL`: MongoDB URL. It should contain the database. E.g. `mongodb://localhost:27017/post`.
  * `TOKEN_VALID_SECS`: seconds of the authorization token validity.
  * `TOKEN_SECRET`: the secret to encrypt the authorization token.
  * `ENCRYPTION_COST`: cost of the encryption of the password, default to `10`.
* Run the application by `yarn run start`.

## Unit tests

* Because of ~~being too lazy~~ the time limitation, I only write a few unit tests as the example.
  * [Workflow unit-tests](test/delete-post/delete-post.test.ts).
  * [Model validation unit-tests](test/common).
