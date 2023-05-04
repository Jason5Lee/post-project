package me.jason5lee.post_kt_vertx_fdm.delete_post

import io.vertx.core.http.HttpMethod
import me.jason5lee.post_kt_vertx_fdm.common.PostId
import me.jason5lee.post_kt_vertx_fdm.common.api.badRequest
import me.jason5lee.post_kt_vertx_fdm.common.utils.*

val api = HttpApi(HttpMethod.DELETE, "/post/{id}") { ctx, workflow: Workflow ->
    val caller = ctx.getCallerIdentity() ?: throw workflow.notCreatorAdmin()
    val id = ctx.pathParameters()["id"] ?: throw badRequest("Missing path parameter `id`")
    workflow.run(caller, PostId(id))

    ctx.respondNoContent()
}

interface ErrorsImpl : Errors {
    override fun notCreatorAdmin(): Exception = HttpException(
        status = 403,
        FailureBody(
            error = Err(
                error = "NOT_CREATOR_ADMIN",
                reason = "Only the creator or an admin can delete a post.",
                message = "Only the creator or an admin can delete a post",
            )
        )
    )

    override fun postNotFound(): Exception = HttpException(
        status = 404,
        FailureBody(
            error = Err(
                error = "POST_NOT_FOUND",
                reason = "The post does not exist",
                message = "The post does not exist",
            )
        )
    )
}
