package me.jason5lee.post_ktor_mongo_fdm.get_post

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_ktor_mongo_fdm.common.PostContent
import me.jason5lee.post_ktor_mongo_fdm.common.PostId
import me.jason5lee.post_ktor_mongo_fdm.common.api.badRequest
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Api
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody
import me.jason5lee.post_ktor_mongo_fdm.common.utils.HttpException
import me.jason5lee.resukt.getOrElse

val api = Api.create(HttpMethod.Get, "/post/{id}") { ctx, workflow: Workflow ->
    val id = ctx.pathParameters()["id"] ?: throw badRequest("Missing path parameter `id`")
    val post = workflow.run(PostId(id)).getOrElse { throw it.asException() }

    val text: String?
    val url: String?
    when (post.content) {
        is PostContent.Text -> {
            text = post.content.value.value
            url = null
        }
        is PostContent.Url -> {
            text = null
            url = post.content.value.value
        }
    }

    ctx.respond(
        HttpStatusCode.OK,
        run {
            @Serializable
            class ResponseBody(
                val creatorId: String,
                val creatorName: String,
                val creationTime: Long,
                val title: String,
                val text: String? = null,
                val url: String? = null,
                val lastModified: Long? = null,
            )

            ResponseBody(
                creatorId = post.creator.id.value,
                creatorName = post.creator.name.value,
                creationTime = post.creationTime.utc,
                title = post.title.value,
                text = text,
                url = url,
                lastModified = post.lastModified?.utc,
            )
        }
    )
}

fun Errors.asException(): Exception =
    when (this) {
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
    }
