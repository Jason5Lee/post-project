# Backend Services

- [post-rust-actix-web-fdm](post-rust-actix-web-fdm): the implementation using Rust, actix-web and sqlx.
- [post-ts-koa-mongo-fdm](post-ts-koa-mongo-fdm): the implementation using NodeJS, TypeScript, Koa and MongoDB.

## What you shouldn't learn from this project

- Authorization. I use a simple token mechanism. It is not secure.
- REST API design. I tried my best, but it may not fitted all the best practices.
- SQL database operation. I don't use ORM in some projects.
- MongoDB database operation. Maybe the setup process is not the best practice.
- Dependencies injection. Because I use vertical slice architecture, I don't use dependency injection. But it may actually worth using.
