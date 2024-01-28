
# post-ktor-mongo

The backend service of post using Kotlin, ktor, and MongoDB.

This implementation uses [my development method](../../mdm.md).

Here is [the API document](../../api-document).

## Deployment

* Setup a MongoDB.
* If you have modified the workflows, run `./gradlew generateRouting`.
    * You can customize the workflow implementation class by modifying the `customWorkflowImpl` value
      in [`generate.kt`](./buildSrc/src/main/kotlin/me/jason5lee/post_ktor_mongo/generate.kt).
* Config by either setting up environment variables or using a `.env` file.
    * `LISTEN_HOST`: listening host.
    * `LISTEN_PORT`: listening port.
    * `MONGO_URL`: MongoDB URL. E.g. `mongodb://localhost:27017/`.
    * `MONGO_DATABASE`: MongoDB database name.
    * `TOKEN_VALID_SECS`: seconds of the authorization token validity.
    * `TOKEN_SECRET`: the secret to encrypt the authorization token.
    * `ADMIN_TOKEN`: the admin token.
    * `ENCRYPTION_COST`: cost of the encryption of the password, default to `10`.
* Run `./gradlew run` to start the server.
* Run `./gradlew buildFatJar` to build a fat jar.

## Error modeling

For illustration purpose, several error modeling methods are used in this project.
In the real world project, you should choose one of them.

### Workflow errors

For most of the workflows, they use the simplified error modeling.
In the domain model, the errors are model as an interface `Errors`, each method of which represents a domain error.
The `Workflow` class implements the `Errors` interface, so that the workflow can trigger the error.

The implementation of the errors is in the `ErrorsImpl` interface in `api.kt` file.
The method implementation contains the information about the response of the error.

The `edit_post` and `get_post` workflows are the exceptions. They use the ideal error modeling.
Which is, using `Result<T, F>` to represents the workflow result, where `T` is the success result type, and `F` is the
domain error type.
For the non-domain errors, they are thrown as exceptions.

## Tests

The example of unit-testing of the workflows and the domain models with both error modeling methods are provided
under [here](src/test/kotlin/me/jason5lee/post_ktor_mongo).
