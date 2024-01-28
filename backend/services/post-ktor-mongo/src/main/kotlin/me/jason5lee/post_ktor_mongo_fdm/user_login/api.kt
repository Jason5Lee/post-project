package me.jason5lee.post_ktor_mongo.user_login

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_ktor_mongo.common.newPassword
import me.jason5lee.post_ktor_mongo.common.newUserName
import me.jason5lee.post_ktor_mongo.common.utils.*

val api = HttpApi(HttpMethod.Post, "/login") { ctx, workflow: Workflow ->
    @Serializable
    class RequestBody(
        val userName: String,
        val password: String,
    )

    val req = ctx.getRequestBody<RequestBody>()
    val query = Query(
        userName = newUserName(req.userName) ?: throw workflow.userNameOrPasswordIncorrect(),
        password = newPassword(req.password) ?: throw workflow.userNameOrPasswordIncorrect(),
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
                token = ctx.generateUserToken(userId, expire),
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
            )
        )
    )
}
