# Post service implementation

The backend service of post using NodeJS, TypeScript, Koa and MongoDB.

This implementation uses [my development method](../../mdm.md).

Here is [the API document](../../api-document).

## Deployment

* Setup a MongoDB.
* This project is package-manager independent. You can you `npm`, `pnpm` or `yarn`. `npm` will be used as the example in this README.
  * The lock file in this project is from `pnpm`. You can remove it if you don't use `pnpm`.
  * If you are using `yarn` with Plug'n'Play, you may want to create a `.gitignore` for yarn-specific files.
* Run `npm install`.
* If you change the workflows, run `npm run generate` to generate `src/addRoutes.ts`.
  * You can customize the implementation class by modifying the `customWorkflowImpl` object in [`generate.js`](./src/common/utils/generate.js) file.
* (Optional) use [create admin tool](../../createadmin) to create admin.
* Config by either setting up environment variables or using a `.env` file.
  * `LISTEN_HOST`: listening host.
  * `LISTEN_PORT`: listening port.
  * `MONGO_URL`: MongoDB URL. It should contain the database. E.g. `mongodb://localhost:27017/post`.
  * `TOKEN_VALID_SECS`: seconds of the authorization token validity.
  * `TOKEN_SECRET`: the secret to encrypt the authorization token.
  * `ENCRYPTION_COST`: cost of the encryption of the password, default to `10`.
* Run the application by `npm run start`.

## Unit tests

Because of ~~being too lazy~~ the time limitation, I only write a few unit tests as the example.
  * [Workflow unit-tests](test/delete-post/delete-post.test.ts).
  * [Model validation unit-tests](test/common).

`npm run test` will run the tests.
