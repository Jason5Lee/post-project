package me.jason5lee.post_ktor_mongo_fdm.delete_post

import io.ktor.http.*
import me.jason5lee.post_ktor_mongo_fdm.common.PostId
import me.jason5lee.post_ktor_mongo_fdm.common.api.badRequest
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody
import me.jason5lee.post_ktor_mongo_fdm.common.utils.HttpApi
import me.jason5lee.post_ktor_mongo_fdm.common.utils.HttpException

val api = HttpApi(HttpMethod.Delete, "/post/{id}") { ctx, workflow: Workflow ->
    val caller = ctx.getCallerIdentity() ?: throw workflow.notCreatorAdmin()
    val id = ctx.pathParameters()["id"] ?: throw badRequest("Missing path parameter `id`")
    workflow.run(caller, PostId(id))

    ctx.respondNoContent()
}

interface ErrorsImpl : Errors {
    override fun notCreatorAdmin(): Exception = HttpException(
        HttpStatusCode.Forbidden,
        FailureBody(
            error = Err(
                error = "NOT_CREATOR_ADMIN",
                reason = "Only the creator or an admin can delete a post.",
                message = "Only the creator or an admin can delete a post",
            )
        )
    )

    override fun postNotFound(): Exception = HttpException(
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