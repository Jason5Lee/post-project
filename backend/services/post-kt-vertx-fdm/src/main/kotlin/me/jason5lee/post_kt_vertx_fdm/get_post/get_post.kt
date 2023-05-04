package me.jason5lee.post_kt_vertx_fdm.get_post

import me.jason5lee.post_ktor_mongo_fdm.common.*
import me.jason5lee.resukt.Result

data class Creator(
    val id: me.jason5lee.post_kt_vertx_fdm.common.UserId,
    val name: me.jason5lee.post_kt_vertx_fdm.common.UserName,
)

data class Post(
    val creator: Creator,
    val creationTime: me.jason5lee.post_kt_vertx_fdm.common.Time,
    val lastModified: me.jason5lee.post_kt_vertx_fdm.common.Time?,
    val title: me.jason5lee.post_kt_vertx_fdm.common.Title,
    val content: me.jason5lee.post_kt_vertx_fdm.common.PostContent,
)

abstract class Workflow {
    abstract suspend fun run(id: me.jason5lee.post_kt_vertx_fdm.common.PostId): Result<Post, Errors>
}

enum class Errors {
    PostNotFound,
}
