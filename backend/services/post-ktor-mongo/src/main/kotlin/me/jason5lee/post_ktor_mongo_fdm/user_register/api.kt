package me.jason5lee.post_ktor_mongo.user_register

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_ktor_mongo.common.api.Invalid
import me.jason5lee.post_ktor_mongo.common.newPassword
import me.jason5lee.post_ktor_mongo.common.newUserName
import me.jason5lee.post_ktor_mongo.common.utils.Err
import me.jason5lee.post_ktor_mongo.common.utils.FailureBody
import me.jason5lee.post_ktor_mongo.common.utils.HttpApi
import me.jason5lee.post_ktor_mongo.common.utils.HttpException

val api = HttpApi(HttpMethod.Post, "/register") { ctx, workflow: Workflow ->
    @Serializable
    class RequestBody(
        val userName: String,
        val password: String,
    )

    val req = ctx.getRequestBody<RequestBody>()
    val output = workflow.run(
        Command(
            userName = newUserName(req.userName) ?: throw HttpException(HttpStatusCode.BadRequest, Invalid.userName),
            password = newPassword(req.password) ?: throw HttpException(HttpStatusCode.BadRequest, Invalid.password),
        )
    )

    ctx.respond(
        HttpStatusCode.Created,
        run {
            ctx.responseHeaders().append("Location", "/user/${output.value}")
            @Serializable
            class ResponseBody(
                val userId: String,
            )
            ResponseBody(
                userId = output.value,
            )
        }
    )
}

interface ErrorsImpl : Errors {
    override fun userNameAlreadyExists(): Exception = HttpException(
        HttpStatusCode.Conflict,
        FailureBody(
            error = Err(
                error = "USER_NAME_ALREADY_EXISTS",
                reason = "The user name already exists",
            )
        )
    )
}
