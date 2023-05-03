# Development Methodology

This document describes my development methodology, which is inspired by Scott Wlaschin's [Domain Modeling](https://pragprog.com/titles/swdddf/domain-modeling-made-functional/) concept, with some personal ideas. This methodology has been applied in the following projects:

- [post-rust-actix-web-fdm](services/post-rust-actix-web-fdm)
- [post-ts-koa-mongo-fdm](services/post-ts-koa-mongo-fdm)
- [post-ktor-mongo-fdm](./services/post-ktor-mongo-fdm)

## Code Structure

The fundamental principle of this methodology is to consider code as documentation. The code should be as informative as possible and can serve as the project documentation.

* We employ [Vertical Slice Architecture](https://jimmybogard.com/vertical-slice-architecture/). All workflow-related code, including business logic and implementation, is placed in a directory named after the workflow. Shared code resides in a `common` directory.
* Each workflow directory includes:
  * A file named after the workflow (or `mod.rs` in Rust, `index.ts` in TypeScript) that contains the domain model, providing a shared model for both programmers and non-programmers. It includes:
    * The structure for the query or command.
    * The workflow definition, optionally including dependent workflows and workflow implementation that calls these dependencies.
    * Domain errors.
  * The `api.<ext>` file, which serves as an API documentation. It includes:
    * The API endpoint.
    * Authentication and authorization details.
    * Instructions for building workflow input from the request, explaining the request fields and their validations.
    * Guidelines for constructing the response from the workflow output, explaining response fields.
    * Error responses.
    This file should be referenced when making API calls or writing API documentation.
  * The `impl.<ext>` (or `deps.rs` in Rust, since `impl` is a keyword) file contains the implementation of the workflow or the dependent workflows.
* The `common` directory contains:
  * The `models.<ext>` (or `mod.rs` in Rust, `index.ts` in TypeScript) file with shared domain models, which utilize newtypes (wrapper types or [branded types](https://www.typescriptlang.org/play?q=370#example/nominal-typing) in TypeScript) over primitive types for business models (e.g., `UserName` as a type wrapping over a string).
    * Newtypes establish the boundary of validated values, preventing unvalidated data from being used implicitly.
    * Request data should be converted into newtypes using a validation function. Database data may be trusted and newtypes can be created without validation.
  * The `api` directory, which contains shared API information, including the responses for each domain model's invalid value errors.
  * Other common utilities.

## Error Design

In JVM languages, domain error is referred to as 'failure' since 'error' already has a specific meaning.

The ideal error representation is `Result<T, E>`, where `T` is a success value type and `E` is an error type. This structure is used for workflow output and domain error. Infrastructure errors (like database timeouts) or potential bugs are thrown as exceptions. However:

* Not all languages support `Result<T, E>` or convenient processing.
  * Writing a lot of match-and-return-error code is cumbersome.
  * Combining with `Future` complicates the use of combinators.
* Not all languages support exceptions.
  * Some languages have a panic-unwind mechanism, but its use is usually discouraged.
* In most cases, the only operation performed on the domain error is to convert it into a response.
  * During testing, the only operation is to compare it with the expected response.

To simplify error handling while still properly modeling errors, some service implementations use a simplified error modeling method. In these cases, a general exception or `Result<T, E>` with a general error type is used, which contains the error response. In the domain modeling, the domain errors are modeled as function definitions that return the general error. These functions' implementations are located in the `api` file.

Validation errors are slightly different, as they require specific handling. They can result in a range of responses, such as a 404 Not Found or 403 Forbidden (for login), among others. It is essential to handle validation errors explicitly.

There are two ways to achieve this. First, the validation function can return a `Result<T, Invalid>` or a specific `ValidationResult<T>`. The caller must handle the error before accessing the validated value. Alternatively, the validation function can accept a parameter that determines the error when the value is invalid. The [post-ts-koa-mongo-fdm](./services/post-ts-koa-mongo-fdm) project uses the second approach, while the others use the first.

The simplified error modeling approach is used in the following projects:

- [post-rust-actix-web-fdm](./services/post-rust-actix-web-fdm)
- [post-ts-koa-mongo-fdm](./services/post-ts-koa-mongo-fdm)
- [post-ktor-mongo-fdm](./services/post-ktor-mongo-fdm) (this project employs both error modeling approaches)
- 