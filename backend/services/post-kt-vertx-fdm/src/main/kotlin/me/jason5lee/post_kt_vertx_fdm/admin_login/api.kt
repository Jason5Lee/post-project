package me.jason5lee.post_kt_vertx_fdm.admin_login

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_kt_vertx_fdm.common.AdminId
import me.jason5lee.post_kt_vertx_fdm.common.Identity
import me.jason5lee.post_kt_vertx_fdm.common.newPassword
import me.jason5lee.post_ktor_mongo_fdm.common.utils.*

val api = HttpApi(HttpMethod.Post, "/admin/login") { ctx, workflow: me.jason5lee.post_kt_vertx_fdm.admin_login.Workflow ->
    @Serializable
    class RequestBody(
        val id: String,
        val password: String,
    )

    val req = ctx.getRequestBody<RequestBody>()
    val query = me.jason5lee.post_kt_vertx_fdm.admin_login.Query(
        id = me.jason5lee.post_kt_vertx_fdm.common.AdminId(req.id),
        password = me.jason5lee.post_kt_vertx_fdm.common.newPassword(req.password)
            .onInvalidThrow { workflow.idOrPasswordIncorrect() },
    )
    val adminId = workflow.run(query)

    val expire = ctx.getTokenExpireTime()

    ctx.respond(
        HttpStatusCode.OK,
        run {
            @Serializable
            class ResponseBody(
                val token: String,
                val expire: Long,
            )
            ResponseBody(
                token = ctx.generateToken(me.jason5lee.post_kt_vertx_fdm.common.Identity.Admin(adminId), expire),
                expire = expire.utc,
            )
        }
    )
}

interface ErrorsImpl : me.jason5lee.post_kt_vertx_fdm.admin_login.Errors {
    override fun idOrPasswordIncorrect(): Exception = HttpException(
        HttpStatusCode.Forbidden,
        FailureBody(
            Err(
                error = "id_or_password_incorrect",
                reason = "The Admin ID or password is incorrect",
                message = "The Admin ID or password is incorrect",
            )
        )
    )
}
