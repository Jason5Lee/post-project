package me.jason5lee.post_kt_vertx_fdm.create_post

import me.jason5lee.post_kt_vertx_fdm.common.PostContent
import me.jason5lee.post_kt_vertx_fdm.common.PostId
import me.jason5lee.post_kt_vertx_fdm.common.Title
import me.jason5lee.post_kt_vertx_fdm.common.UserId

data class Command(
    val title: me.jason5lee.post_kt_vertx_fdm.common.Title,
    val content: me.jason5lee.post_kt_vertx_fdm.common.PostContent,
)

abstract class Workflow : Errors {
    abstract suspend fun run(caller: me.jason5lee.post_kt_vertx_fdm.common.UserId, input: Command): me.jason5lee.post_kt_vertx_fdm.common.PostId
}

interface Errors {
    fun duplicateTitle(): Exception
}
