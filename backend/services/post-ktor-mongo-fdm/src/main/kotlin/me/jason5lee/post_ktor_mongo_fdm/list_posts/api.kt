package me.jason5lee.post_ktor_mongo_fdm.list_posts

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_ktor_mongo_fdm.common.*
import me.jason5lee.post_ktor_mongo_fdm.common.api.Invalid
import me.jason5lee.post_ktor_mongo_fdm.common.api.badRequest
import me.jason5lee.post_ktor_mongo_fdm.common.utils.*

val api = HttpApi(HttpMethod.Get, "/post") { ctx, workflow: Workflow ->
    if (ctx.pathParameters()["search"] != null) {
        throw searchNotImplemented()
    }
    val pageParam = ctx.pathParameters()["page"] ?: throw badRequest("Missing path parameter `page`")
    val pageSizeParam = ctx.pathParameters()["pageSize"] ?: throw badRequest("Missing path parameter `pageSize`")

    val page = newPage(pageParam.toIntOrNull() ?: throw badRequest("Invalid path parameter `page`"))
        ?: throw HttpException(HttpStatusCode.BadRequest, Invalid.page)
    val pageSize =
        newPageSize(pageSizeParam.toIntOrNull() ?: throw badRequest("Invalid path parameter `pageSize`"))
            ?: throw HttpException(HttpStatusCode.BadRequest, Invalid.pageSize)
    val creator = ctx.pathParameters()["creator"]?.let { UserId(it) }

    val output = workflow.run(Query(page, pageSize, creator))
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
                val total: Long,
                val posts: List<PostBody>,
            )
            ResponseBody(
                total = output.total,
                posts = output.posts.map { post ->
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

interface ErrorsImpl : Errors {
    override fun creatorNotFound(): Exception = HttpException(
        HttpStatusCode.NotFound,
        FailureBody(
            error = Err(
                error = "CREATOR_NOT_FOUND",
                reason = "The creator does not exist",
            )
        )
    )
}

fun searchNotImplemented(): Exception = HttpException(
    HttpStatusCode.NotImplemented,
    FailureBody(
        error = Err(
            error = "SEARCH_NOT_IMPLEMENTED",
            reason = "Search is not implemented",
        )
    )
)
