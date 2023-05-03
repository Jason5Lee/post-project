package me.jason5lee.post_ktor_mongo_fdm.get_user

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_ktor_mongo_fdm.common.UserId
import me.jason5lee.post_ktor_mongo_fdm.common.api.badRequest
import me.jason5lee.post_ktor_mongo_fdm.common.utils.HttpApi
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody
import me.jason5lee.post_ktor_mongo_fdm.common.utils.HttpException

val api = HttpApi(HttpMethod.Get, "/user/{id}") { ctx, workflow: Workflow ->
    val id = ctx.pathParameters()["id"] ?: throw badRequest("Missing path parameter `id`")
    val user = workflow.run(UserId(id))
    ctx.respond(
        HttpStatusCode.OK,
        run {
            @Serializable
            class ResponseBody(
                val userName: String,
                val creationTime: Long,
            )

            ResponseBody(
                userName = user.name.value,
                creationTime = user.creationTime.utc,
            )
        }
    )
}

interface ErrorsImpl : Errors {
    override fun userNotFound(): Exception = HttpException(
        HttpStatusCode.NotFound,
        FailureBody(
            error = Err(
                error = "USER_NOT_FOUND",
                reason = "The user does not exist",
                message = "The user does not exist",
            )
        )
    )
}
