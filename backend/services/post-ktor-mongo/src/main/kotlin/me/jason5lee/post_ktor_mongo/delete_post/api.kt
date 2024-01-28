package me.jason5lee.post_ktor_mongo.delete_post

import io.ktor.http.*
import me.jason5lee.post_ktor_mongo.common.PostId
import me.jason5lee.post_ktor_mongo.common.api.badRequest
import me.jason5lee.post_ktor_mongo.common.utils.Err
import me.jason5lee.post_ktor_mongo.common.utils.FailureBody
import me.jason5lee.post_ktor_mongo.common.utils.HttpApi
import me.jason5lee.post_ktor_mongo.common.utils.HttpException

val api = HttpApi(HttpMethod.Delete, "/post/{id}") { ctx, workflow: Workflow ->
    val caller = ctx.getCallerIdentity() ?: throw workflow.notCreatorAdmin()
    val id = ctx.pathParameters()["id"] ?: throw badRequest("Missing path parameter `id`")
    workflow.run(caller, PostId(id))

    ctx.respondNoContent()
}

interface FailuresImpl : Failures {
    override fun notCreatorAdmin(): Exception = HttpException(
        HttpStatusCode.Forbidden,
        FailureBody(
            error = Err(
                error = "NOT_CREATOR_ADMIN",
                reason = "Only the creator or an admin can delete a post.",
            )
        )
    )

    override fun postNotFound(): Exception = HttpException(
        HttpStatusCode.NotFound,
        FailureBody(
            error = Err(
                error = "POST_NOT_FOUND",
                reason = "The post does not exist",
            )
        )
    )
}