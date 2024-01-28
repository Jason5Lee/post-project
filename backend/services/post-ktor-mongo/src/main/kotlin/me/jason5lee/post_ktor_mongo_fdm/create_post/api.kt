package me.jason5lee.post_ktor_mongo.create_post

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_ktor_mongo.common.*
import me.jason5lee.post_ktor_mongo.common.api.Invalid
import me.jason5lee.post_ktor_mongo.common.utils.*

val api = HttpApi(HttpMethod.Post, "/post") { ctx, workflow: Workflow ->
    val caller = ctx.getCallerIdentity() as? Identity.User ?: throw userOnly()

    @Serializable
    class RequestBody(
        val title: String,
        val text: String? = null,
        val url: String? = null,
    )

    val req = ctx.getRequestBody<RequestBody>()
    val command = Command(
        title = newTitle(req.title) ?: throw HttpException(HttpStatusCode.BadRequest, Invalid.title),
        content = if (req.text != null && req.url == null) {
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
    val postId = workflow.run(caller.id, command)

    ctx.respond(HttpStatusCode.Created, run {
        ctx.responseHeaders().append("Location", "/post/${postId.value}")

        @Serializable
        class ResponseBody(
            val postId: String,
        )
        ResponseBody(
            postId = postId.value,
        )
    })
}

interface ErrorsImpl : Errors {
    override fun duplicateTitle(): Exception = HttpException(
        HttpStatusCode.Conflict, FailureBody(
            error = Err(
                error = "DUPLICATE_TITLE",
                reason = "The title is already used",
            )
        )
    )
}

fun textUrlExactOne(): Exception = HttpException(
    HttpStatusCode.UnprocessableEntity, FailureBody(
        error = Err(
            error = "TEXT_URL_EXACT_ONE",
            reason = "Exactly one of `text` and `url` must be provided",
        )
    )
)

fun userOnly(): Exception = HttpException(
    HttpStatusCode.Forbidden, FailureBody(
        error = Err(
            error = "USER_ONLY",
            reason = "Only user can create post",
        )
    )
)
