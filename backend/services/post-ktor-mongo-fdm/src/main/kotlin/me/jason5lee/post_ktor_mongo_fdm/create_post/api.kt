package me.jason5lee.post_ktor_mongo_fdm.create_post

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_ktor_mongo_fdm.common.Title
import me.jason5lee.post_ktor_mongo_fdm.common.Identity
import me.jason5lee.post_ktor_mongo_fdm.common.PostContent
import me.jason5lee.post_ktor_mongo_fdm.common.TextPostContent
import me.jason5lee.post_ktor_mongo_fdm.common.UrlPostContent
import me.jason5lee.post_ktor_mongo_fdm.common.api.clientBugMessage
import me.jason5lee.post_ktor_mongo_fdm.common.utils.*

val api = Api.create(HttpMethod.Put, "/post") { ctx, workflow: Workflow ->
    val caller = ctx.getCallerIdentity() as? Identity.User ?: throw userOnly()

    @Serializable
    class RequestBody(
        val title: String,
        val text: String? = null,
        val url: String? = null,
    )
    val req = ctx.getRequestBody<RequestBody>()
    val command = Command(
        title = Title.validate(req.title, OnInvalidRespond(HttpStatusCode.UnprocessableEntity)),
        content =
            if (req.text != null && req.url == null) {
                PostContent.Text(TextPostContent.validate(req.text).onInvalidRespond(HttpStatusCode.UnprocessableEntity))
            } else if (req.text == null && req.url != null) {
                PostContent.Url(UrlPostContent.validate(req.url).onInvalidRespond(HttpStatusCode.UnprocessableEntity))
            } else {
                throw textUrlExactOne()
            }
    )
    val postId = workflow.run(caller.id, command)

    ctx.respond(
        HttpStatusCode.Created,
        run {
            ctx.responseHeaders().append("Location", "/post/${postId.value}")

            @Serializable
            class ResponseBody(
                val postId: String,
            )
            ResponseBody(
                postId = postId.value,
            )
        }
    )
}

interface ErrorsImpl : Errors {
    override fun duplicateTitle(): Exception = HttpException(
        HttpStatusCode.Conflict,
        FailureBody(
            error = Err(
                error = "DUPLICATE_TITLE",
                reason = "The title is already used",
                message = "The title is already used",
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

fun userOnly(): Exception = HttpException(
    HttpStatusCode.Forbidden,
    FailureBody(
        error = Err(
            error = "USER_ONLY",
            reason = "Only user can create post",
            message = "Only user can create post",
        )
    )
)
