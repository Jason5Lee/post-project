package me.jason5lee.post_ktor_mongo_fdm.user_register

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_ktor_mongo_fdm.common.Password
import me.jason5lee.post_ktor_mongo_fdm.common.UserName
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Api
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody
import me.jason5lee.post_ktor_mongo_fdm.common.utils.HttpException

val api = Api.create(HttpMethod.Post, "/register") { ctx, workflow: Workflow ->
    @Serializable
    class RequestBody(
        val userName: String,
        val password: String,
    )

    val req = ctx.getRequestBody<RequestBody>()
    val output = workflow.run(Command(
        userName = UserName.validate(req.userName).onInvalidRespond(HttpStatusCode.UnprocessableEntity),
        password = Password.validate(req.password).onInvalidRespond(HttpStatusCode.UnprocessableEntity),
    ))

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
                message = "The user name already exists",
            )
        )
    )
}
