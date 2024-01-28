# Backend Services

- [post-rust-actix-web](post-rust-actix-web): the implementation using Rust, actix-web and sqlx.
- [post-ts-koa-mongo](post-ts-koa-mongo): the implementation using NodeJS, TypeScript, Koa and MongoDB.
- [post-ktor-mongo](post-ktor-mongo): the implementation using Kotlin, ktor, and MongoDB. 

## What you shouldn't learn from this project

- Authorization. I use a simple token mechanism. It is the most secure solution.
- REST API design. I tried my best, but it may not fitted all the best practices.
- SQL database operation. I don't use ORM in some projects.
- MongoDB database operation. Maybe the setup process is not the best practice.
- Dependencies injection. Because I use vertical slice architecture, I don't use any dependency injection framework. But it may actually worth it.
