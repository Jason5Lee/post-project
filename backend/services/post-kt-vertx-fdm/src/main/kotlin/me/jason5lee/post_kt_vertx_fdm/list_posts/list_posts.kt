package me.jason5lee.post_kt_vertx_fdm.list_posts

import me.jason5lee.post_ktor_mongo_fdm.common.*

sealed class Condition {
    data class Before(val time: me.jason5lee.post_kt_vertx_fdm.common.Time) : Condition()
    data class After(val time: me.jason5lee.post_kt_vertx_fdm.common.Time) : Condition()
}

data class Query(
    val creator: me.jason5lee.post_kt_vertx_fdm.common.UserId?,
    val condition: Condition?,
    val size: me.jason5lee.post_kt_vertx_fdm.common.Size,
)

data class Post(
    val id: me.jason5lee.post_kt_vertx_fdm.common.PostId,
    val title: me.jason5lee.post_kt_vertx_fdm.common.Title,
    val creator: Creator,
    val creationTime: me.jason5lee.post_kt_vertx_fdm.common.Time,
)

data class Creator(
    val id: me.jason5lee.post_kt_vertx_fdm.common.UserId,
    val name: me.jason5lee.post_kt_vertx_fdm.common.UserName,
)

abstract class Workflow : Errors {
    abstract suspend fun run(input: Query): List<Post>
}

interface Errors {
    fun creatorNotFound(): Exception
}
