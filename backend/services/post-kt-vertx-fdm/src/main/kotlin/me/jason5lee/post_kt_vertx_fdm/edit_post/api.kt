package me.jason5lee.post_kt_vertx_fdm.edit_post

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_ktor_mongo_fdm.common.*
import me.jason5lee.post_kt_vertx_fdm.common.api.badRequest
import me.jason5lee.post_kt_vertx_fdm.common.api.clientBugMessage
import me.jason5lee.post_ktor_mongo_fdm.common.utils.HttpApi
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody
import me.jason5lee.post_ktor_mongo_fdm.common.utils.HttpException
import me.jason5lee.resukt.fold

val api = HttpApi(HttpMethod.Patch, "/post/{id}") { ctx, workflow: Workflow ->
    val caller = ctx.getCallerIdentity() as? me.jason5lee.post_kt_vertx_fdm.common.Identity.User ?: throw Errors.NotCreator.asException()
    val id = ctx.pathParameters()["id"] ?: throw me.jason5lee.post_kt_vertx_fdm.common.api.badRequest("Missing path parameter `id`")

    @Serializable
    class RequestBody(
        val text: String? = null,
        val url: String? = null,
    )

    val req = ctx.getRequestBody<RequestBody>()
    val command = Command(
        id = me.jason5lee.post_kt_vertx_fdm.common.PostId(id),
        newContent = if (req.text != null && req.url == null) {
            me.jason5lee.post_kt_vertx_fdm.common.PostContent.Text(
                me.jason5lee.post_kt_vertx_fdm.common.newTextPostContent(
                    req.text
                ).onInvalidRespond(HttpStatusCode.UnprocessableEntity))
        } else if (req.text == null && req.url != null) {
            me.jason5lee.post_kt_vertx_fdm.common.PostContent.Url(
                me.jason5lee.post_kt_vertx_fdm.common.newUrlPostContent(
                    req.url
                ).onInvalidRespond(HttpStatusCode.UnprocessableEntity))
        } else {
            throw textUrlExactOne()
        }
    )

    workflow.run(caller.id, command).fold(
        onSuccess = { ctx.respondNoContent() },
        onFailure = { failure -> throw failure.asException() },
    )
}

fun Errors.asException(): Exception = when (this) {
    Errors.PostNotFound -> HttpException(
        HttpStatusCode.NotFound,
        FailureBody(
            error = Err(
                error = "POST_NOT_FOUND",
                reason = "The post does not exist",
                message = "The post does not exist",
            )
        )
    )

    Errors.NotCreator -> HttpException(
        HttpStatusCode.Forbidden,
        FailureBody(
            error = Err(
                error = "NOT_CREATOR",
                reason = "You are not the creator of the post",
                message = "You are not the creator of the post",
            )
        )
    )

    Errors.TypeDiff -> HttpException(
        HttpStatusCode.BadRequest,
        FailureBody(
            error = Err(
                error = "TYPE_DIFF",
                reason = "The type of the post cannot be changed",
                message = "The type of the post cannot be changed",
            )
        )
    )
}

fun textUrlExactOne(): Exception = HttpException(
    HttpStatusCode.UnprocessableEntity,
    FailureBody(
        error = Err(
            error = "TEXT_URL_EXACT_ONE",
            reason = "Exactly one of `text` and `url` must be provided",
            message = me.jason5lee.post_kt_vertx_fdm.common.api.clientBugMessage,
        )
    )
)
