package me.jason5lee.post_kt_vertx_fdm.create_post

import io.vertx.core.http.HttpMethod
import kotlinx.serialization.Serializable
import me.jason5lee.post_kt_vertx_fdm.common.*
import me.jason5lee.post_kt_vertx_fdm.common.api.clientBugMessage
import me.jason5lee.post_kt_vertx_fdm.common.utils.*

val api = HttpApi(HttpMethod.POST, "/post") { ctx, workflow: Workflow ->
    val caller = ctx.getCallerIdentity() as? Identity.User ?: throw userOnly()

    @Serializable
    class RequestBody(
        val title: String,
        val text: String? = null,
        val url: String? = null,
    )

    val req = ctx.getRequestBody<RequestBody>()
    val command = Command(
        title = newTitle(
            req.title,
            OnInvalidRespond(statusCode = 422)
        ),
        content =
        if (req.text != null && req.url == null) {
            PostContent.Text(
                newTextPostContent(
                    req.text
                ).onInvalidRespond(statusCode = 422))
        } else if (req.text == null && req.url != null) {
            PostContent.Url(
                newUrlPostContent(
                    req.url
                ).onInvalidRespond(statusCode = 422))
        } else {
            throw textUrlExactOne()
        }
    )
    val postId = workflow.run(caller.id, command)

    ctx.respond(
        statusCode = 201,
        run {
            ctx.responseHeaders().add("Location", "/post/${postId.value}")

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
        status = 409,
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
    status = 422,
    FailureBody(
        error = Err(
            error = "TEXT_URL_EXACT_ONE",
            reason = "Exactly one of `text` and `url` must be provided",
            message = clientBugMessage,
        )
    )
)

fun userOnly(): Exception = HttpException(
    status = 403,
    FailureBody(
        error = Err(
            error = "USER_ONLY",
            reason = "Only user can create post",
            message = "Only user can create post",
        )
    )
)
