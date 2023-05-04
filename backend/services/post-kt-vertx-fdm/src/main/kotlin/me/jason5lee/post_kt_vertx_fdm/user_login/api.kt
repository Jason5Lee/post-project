package me.jason5lee.post_kt_vertx_fdm.user_login

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_kt_vertx_fdm.common.Identity
import me.jason5lee.post_kt_vertx_fdm.common.newPassword
import me.jason5lee.post_kt_vertx_fdm.common.newUserName
import me.jason5lee.post_ktor_mongo_fdm.common.utils.*

val api = HttpApi(HttpMethod.Post, "/login") { ctx, workflow: Workflow ->
    @Serializable
    class RequestBody(
        val userName: String,
        val password: String,
    )

    val req = ctx.getRequestBody<RequestBody>()
    val query = Query(
        userName = me.jason5lee.post_kt_vertx_fdm.common.newUserName(req.userName).onInvalidThrow { workflow.userNameOrPasswordIncorrect() },
        password = me.jason5lee.post_kt_vertx_fdm.common.newPassword(req.password).onInvalidThrow { workflow.userNameOrPasswordIncorrect() },
    )
    val userId = workflow.run(query)

    val expire = ctx.getTokenExpireTime()
    ctx.respond(
        HttpStatusCode.OK,
        run {
            @Serializable
            class ResponseBody(
                val id: String,
                val expire: Long,
                val token: String,
            )

            ResponseBody(
                id = userId.value,
                expire = expire.utc,
                token = ctx.generateToken(me.jason5lee.post_kt_vertx_fdm.common.Identity.User(userId), expire),
            )
        }
    )
}

interface ErrorsImpl : Errors {
    override fun userNameOrPasswordIncorrect(): Exception = HttpException(
        HttpStatusCode.Forbidden,
        FailureBody(
            error = Err(
                error = "USER_NAME_OR_PASSWORD_INCORRECT",
                reason = "The user name or password is incorrect",
                message = "The user name or password is incorrect",
            )
        )
    )
}
