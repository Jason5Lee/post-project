package me.jason5lee.post_kt_vertx_fdm.delete_post

import me.jason5lee.post_kt_vertx_fdm.common.Identity
import me.jason5lee.post_kt_vertx_fdm.common.PostId
import me.jason5lee.post_kt_vertx_fdm.common.UserId

typealias Command = me.jason5lee.post_kt_vertx_fdm.common.PostId

abstract class Workflow : Errors {
    suspend fun run(caller: me.jason5lee.post_kt_vertx_fdm.common.Identity, input: Command) {
        val auth = (caller is me.jason5lee.post_kt_vertx_fdm.common.Identity.Admin) || (caller is me.jason5lee.post_kt_vertx_fdm.common.Identity.User && caller.id == getPostCreator(input))
        if (!auth) {
            throw notCreatorAdmin()
        }
        deletePost(input)
    }

    abstract suspend fun getPostCreator(post: me.jason5lee.post_kt_vertx_fdm.common.PostId): me.jason5lee.post_kt_vertx_fdm.common.UserId
    abstract suspend fun deletePost(post: me.jason5lee.post_kt_vertx_fdm.common.PostId)
}

interface Errors {
    fun notCreatorAdmin(): Exception
    fun postNotFound(): Exception
}
