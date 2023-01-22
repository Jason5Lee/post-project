package me.jason5lee.post_ktor_mongo_fdm.edit_post

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_ktor_mongo_fdm.common.*
import me.jason5lee.post_ktor_mongo_fdm.common.api.badRequest
import me.jason5lee.post_ktor_mongo_fdm.common.api.clientBugMessage
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Api
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody
import me.jason5lee.post_ktor_mongo_fdm.common.utils.HttpException
import me.jason5lee.resukt.fold

val api = Api.create(HttpMethod.Post, "/post/{id}") { ctx, workflow: Workflow ->
    val caller = ctx.getCallerIdentity() as? Identity.User ?: throw Errors.NotCreator.asException()
    val id = ctx.pathParameters()["id"] ?: throw badRequest("Missing path parameter `id`")

    @Serializable
    class RequestBody(
        val text: String? = null,
        val url: String? = null,
    )
    val req = ctx.getRequestBody<RequestBody>()
    val command = Command(
        id = PostId(id),
        newContent = if (req.text != null && req.url == null) {
            PostContent.Text(TextPostContent.validate(req.text).onInvalidRespond(HttpStatusCode.UnprocessableEntity))
        } else if (req.text == null && req.url != null) {
            PostContent.Url(UrlPostContent.validate(req.url).onInvalidRespond(HttpStatusCode.UnprocessableEntity))
        } else {
            throw textUrlExactOne()
        }
    )

    workflow.run(caller.id, command).fold(
        onSuccess = { ctx.respondNoContent() },
        onFailure = { failure -> throw failure.asException() },
    )
}

fun Errors.asException(): Exception = when(this) {
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
            message = clientBugMessage,
        )
    )
)
