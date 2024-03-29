package me.jason5lee.post_ktor_mongo.edit_post

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_ktor_mongo.common.*
import me.jason5lee.post_ktor_mongo.common.api.Invalid
import me.jason5lee.post_ktor_mongo.common.api.badRequest
import me.jason5lee.post_ktor_mongo.common.utils.Err
import me.jason5lee.post_ktor_mongo.common.utils.FailureBody
import me.jason5lee.post_ktor_mongo.common.utils.HttpApi
import me.jason5lee.post_ktor_mongo.common.utils.HttpException
import me.jason5lee.resukt.fold

val api = HttpApi(HttpMethod.Patch, "/post/{id}") { ctx, workflow: Workflow ->
    val caller = ctx.getCallerIdentity() as? Identity.User ?: throw Failures.NotCreator.asException()
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
            PostContent.Text(
                newTextPostContent(req.text) ?: throw HttpException(HttpStatusCode.BadRequest, Invalid.textPostContent)
            )
        } else if (req.text == null && req.url != null) {
            PostContent.Url(
                newUrlPostContent(req.url) ?: throw HttpException(HttpStatusCode.BadRequest, Invalid.urlPostContent)
            )
        } else {
            throw textUrlExactOne()
        }
    )

    workflow.run(caller.id, command).fold(
        onSuccess = { ctx.respondNoContent() },
        onFailure = { failure -> throw failure.asException() },
    )
}

fun Failures.asException(): Exception = when (this) {
    Failures.PostNotFound -> HttpException(
        HttpStatusCode.NotFound,
        FailureBody(
            error = Err(
                error = "POST_NOT_FOUND",
                reason = "The post does not exist",
            )
        )
    )

    Failures.NotCreator -> HttpException(
        HttpStatusCode.Forbidden,
        FailureBody(
            error = Err(
                error = "NOT_CREATOR",
                reason = "You are not the creator of the post",
            )
        )
    )

    Failures.TypeDiff -> HttpException(
        HttpStatusCode.BadRequest,
        FailureBody(
            error = Err(
                error = "TYPE_DIFF",
                reason = "The type of the post cannot be changed",
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
        )
    )
)
