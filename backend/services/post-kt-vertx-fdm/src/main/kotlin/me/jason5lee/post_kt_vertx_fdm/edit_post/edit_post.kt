package me.jason5lee.post_kt_vertx_fdm.edit_post

import me.jason5lee.post_kt_vertx_fdm.common.PostContent
import me.jason5lee.post_kt_vertx_fdm.common.PostId
import me.jason5lee.post_kt_vertx_fdm.common.UserId
import me.jason5lee.resukt.Result
import me.jason5lee.resukt.andThen

data class Command(
    val id: me.jason5lee.post_kt_vertx_fdm.common.PostId,
    val newContent: me.jason5lee.post_kt_vertx_fdm.common.PostContent,
)

abstract class Workflow {
    suspend fun run(caller: me.jason5lee.post_kt_vertx_fdm.common.UserId, input: Command): Result<Unit, Errors> =
        checkUserIsCreatorAndContentHasTheSameType(input.id, caller, input.newContent).andThen {
            updatePost(input.id, input.newContent)
        }

    abstract suspend fun checkUserIsCreatorAndContentHasTheSameType(
        post: me.jason5lee.post_kt_vertx_fdm.common.PostId,
        user: me.jason5lee.post_kt_vertx_fdm.common.UserId,
        content: me.jason5lee.post_kt_vertx_fdm.common.PostContent
    ): Result<Unit, Errors>

    abstract suspend fun updatePost(post: me.jason5lee.post_kt_vertx_fdm.common.PostId, newContent: me.jason5lee.post_kt_vertx_fdm.common.PostContent): Result<Unit, Errors>
}

enum class Errors {
    PostNotFound,
    NotCreator,
    TypeDiff,
}
