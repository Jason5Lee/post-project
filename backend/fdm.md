# My development method based on functional domain modeling

Here is my development method based on [the Domain Modeling idea from Scott Wlaschin](https://pragprog.com/titles/swdddf/domain-modeling-made-functional/) with some of my own ideas. It is used in the following projects.

- [post-rust-actix-web-fdm](services/post-rust-actix-web-fdm)
- [post-ts-koa-mongo-fdm](services/post-ts-koa-mongo-fdm)
- [post-ktor-mongo-fdm](./services/post-ktor-mongo-fdm).

## Code Structure

This development method utilizes code-as-documentation. The information contained in the code is as close as possible to the documentation.

* Using [the Vertical Slice Architecture](https://jimmybogard.com/vertical-slice-architecture/). The code about a workflow, including the business logic and implementation, is in the directory with the workflow name. The common code is in a specific `common` directory.
* Under the workflow directory
    * The file with the workflow name (or `mod.rs` in Rust, `index.ts` in TypeScript) contains the domain model. They represent shared model for both programmers and non-programmers. It includes
      * The query or command structure.
      * The workflow definition,
        * and optionally, the dependent workflows and the workflow implementation which calls the dependency.
        * The dependent workflows are the boundary of the business domain. For the simple workflows, workflow itself is the boundary.
      * The domain errors.
    * `api.<ext>` file includes the information of the API. This file can serve as API documentation. It includes
      * The API endpoint.
      * Authentication and authorization.
      * How the workflow input is built from the request. It explains the meaning of the request fields. It also shows the validations of the request.
      * How the response is built from the workflow output. It explains the meaning of the response fields.
      * The response of a certain error.

      If you are the client side that need to call the API
      or you are writing the API document you can refer to these code.
    * `impl.<ext>` (or `deps.rs` in Rust since `impl` is a keyword) file includes the implementation of the workflow or the dependent workflows.
* Under the `common` directory
    * `models.<ext>` (or `mod.rs` in Rust, `index.ts` in TypeScript) file includes the shared domain models.
      * It uses newtypes (which can be wrapper types or [branded types](https://www.typescriptlang.org/play?q=370#example/nominal-typing) in TypeScript) over primitive types to represent business model. For example, a type `UserName` that wraps over a string.
        * The newtypes are the boundary of validated values. You cannot implicitly use an unvalidated `string` as a `UserName`, for example.
        * The values from the requests should be converted into the newtypes by the validation function. The values from the database may be trusted in which case the newtypes can be created without validation.
    * `api` directory includes the shared information of the API.
      * It contains the response of the invalid value errors of each domain model.
    * Some other common utilities.

## Error Design

Note: in the JVM languages, the domain error is refered as failure since error already has a meaning in JVM.

The ideal way to represent an error is to use `Result<T, E>`, which represents either a success value with type `T` or a error with type `E`, for the workflow output and domain error. The errors of infrastructure (such as database timeout) or potential bug are thrown as exception. However,

* Not every language supports `Result<T, E>` and convienient processing.
  * I don't like to write a bunch of match-and-return-error.
  * It is harder to use the combinator when it is combined with `Future`.
* Not every language supports exception.
  * Even some languages have panic-unwind mechanism, in most of the cases, it is not encouraged.
* In most cases, the only operation on the domain error is to convert it into the response.
  * And the only operation in testing is to compare with the expected response.

So, in order to make things simpler while still modeling the errors properly, a simplified error modeling is used in some service implementations. In these implementations, we only use a general exception or `Result<T, E>` with a general error type which contains the response of the error. In the domain modeling, the domain errors are modeled as the definition of the functions that return the general error. The implementation of these functions are in the `api` file.

Validation errors are a bit special. There are different operations on them. We may want to respond the error, treat it as a 404 Not Found, 403 Forbidden (for login), or other operation. It should be enforced to explicitly handle the invalidation error.

There are two ways. First, the validation function returns a `Result<T, Invalid>` or a specific `ValidationResult<T>`. The caller need to handle the error before getting the validated value. Second, the validation function accepts a parameter which determines the error when the value is invalid. The [post-ts-koa-mongo-fdm](./services/post-ts-koa-mongo-fdm) uses the second way while the others use the first way.

The simplified error modeling is used in the following implementations.

- [post-rust-actix-web-fdm](./services/post-rust-actix-web-fdm).
- [post-ts-koa-mongo-fdm](./services/post-ts-koa-mongo-fdm).
- [post-ktor-mongo-fdm](./services/post-ktor-mongo-fdm).
