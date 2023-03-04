package me.jason5lee.post_ktor_mongo_fdm.list_posts

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_ktor_mongo_fdm.common.Time
import me.jason5lee.post_ktor_mongo_fdm.common.UserId
import me.jason5lee.post_ktor_mongo_fdm.common.api.InvalidSize
import me.jason5lee.post_ktor_mongo_fdm.common.api.InvalidTime
import me.jason5lee.post_ktor_mongo_fdm.common.api.clientBugMessage
import me.jason5lee.post_ktor_mongo_fdm.common.newSize
import me.jason5lee.post_ktor_mongo_fdm.common.newTime
import me.jason5lee.post_ktor_mongo_fdm.common.utils.*

val api = Api.create(HttpMethod.Get, "/post") { ctx, workflow: Workflow ->
    val condition = run {
        val beforeParam = ctx.pathParameters()["before"]
        val afterParam = ctx.pathParameters()["after"]
        if (beforeParam != null) {
            if (afterParam != null) {
                throw bothBeforeAfter()
            }
            Condition.Before(validateTimeParam(beforeParam, errorPrefix = "BEFORE_"))
        } else if (afterParam != null) {
            Condition.After(validateTimeParam(afterParam, errorPrefix = "AFTER_"))
        } else {
            null
        }
    }
    val size = (
            ctx.pathParameters()["size"]?.let { sizeParam ->
                sizeParam.toIntOrNull()?.let { newSize(it) }
                    ?: ValidationResult.Invalid(InvalidSize.nonPositiveInteger)
            } ?: newSize(null)
            ).onInvalidRespond(HttpStatusCode.UnprocessableEntity)
    val creator = ctx.pathParameters()["creator"]?.let { UserId(it) }

    val output = workflow.run(Query(creator, condition, size))
    ctx.respond(
        HttpStatusCode.OK,
        run {
            @Serializable
            class PostBody(
                val id: String,
                val title: String,
                val creatorId: String,
                val creatorName: String,
                val creationTime: Long,
            )

            @Serializable
            class ResponseBody(
                val posts: List<PostBody>,
            )
            ResponseBody(
                posts = output.map { post ->
                    PostBody(
                        id = post.id.value,
                        title = post.title.value,
                        creatorId = post.creator.id.value,
                        creatorName = post.creator.name.value,
                        creationTime = post.creationTime.utc,
                    )
                }
            )
        }
    )
}

private fun validateTimeParam(param: String, errorPrefix: String): Time =
    (param
        .toLongOrNull()
        ?.let { newTime(it) }
        ?: ValidationResult.Invalid(InvalidTime.invalid))
        .onInvalidRespond(HttpStatusCode.UnprocessableEntity, errorPrefix)

interface ErrorsImpl : Errors {
    override fun creatorNotFound(): Exception = HttpException(
        HttpStatusCode.NotFound,
        FailureBody(
            error = Err(
                error = "CREATOR_NOT_FOUND",
                reason = "The creator does not exist",
                message = "The creator does not exist",
            )
        )
    )
}

fun bothBeforeAfter(): Exception = HttpException(
    HttpStatusCode.BadRequest,
    FailureBody(
        error = Err(
            error = "BOTH_BEFORE_AFTER",
            reason = "Only one of `before` and `after` can be specified",
            message = clientBugMessage,
        )
    )
)
