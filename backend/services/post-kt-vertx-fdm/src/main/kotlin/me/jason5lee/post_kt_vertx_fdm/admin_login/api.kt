package me.jason5lee.post_kt_vertx_fdm.admin_login

import io.vertx.core.http.HttpMethod
import kotlinx.serialization.Serializable
import me.jason5lee.post_kt_vertx_fdm.common.*
import me.jason5lee.post_kt_vertx_fdm.common.utils.*

val api = HttpApi(HttpMethod.POST, "/admin/login") { ctx, workflow: Workflow ->
    @Serializable
    class RequestBody(
        val id: String,
        val password: String,
    )

    val req = ctx.getRequestBody<RequestBody>()
    val query = Query(
        id = AdminId(req.id),
        password = newPassword(req.password)
            .onInvalidThrow { workflow.idOrPasswordIncorrect() },
    )
    val adminId = workflow.run(query)

    val expire = ctx.getTokenExpireTime()

    ctx.respond(
        statusCode = 200,
        body = run {
            @Serializable
            class ResponseBody(
                val token: String,
                val expire: Long,
            )
            ResponseBody(
                token = ctx.generateToken(Identity.Admin(adminId), expire),
                expire = expire.utc,
            )
        }
    )
}

interface FailuresImpl : Failures {
    override fun idOrPasswordIncorrect(): Exception = HttpException(
        status = 403,
        body = FailureBody(
            Err(
                error = "id_or_password_incorrect",
                reason = "The Admin ID or password is incorrect",
                message = "The Admin ID or password is incorrect",
            )
        )
    )
}
