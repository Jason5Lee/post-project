package me.jason5lee.post_kt_vertx_fdm.get_identity

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.jason5lee.post_ktor_mongo_fdm.common.utils.HttpApi

val api = HttpApi(HttpMethod.Get, "/identity") { ctx, workflow: Workflow ->
    val caller = ctx.getCallerIdentity()
    val identityInfo = workflow.run(caller)

    ctx.respond(
        HttpStatusCode.OK,
        run {
            @Serializable
            class UserResponse(val id: String, val name: String)

            @Serializable
            class AdminResponse(val id: String)

            @Serializable
            class ResponseBody(val user: UserResponse? = null, val admin: AdminResponse? = null)

            when (identityInfo) {
                is IdentityInfo.User -> ResponseBody(
                    user = UserResponse(
                        id = identityInfo.id.value,
                        name = identityInfo.name.value,
                    ),
                )

                is IdentityInfo.Admin -> ResponseBody(
                    admin = AdminResponse(
                        id = identityInfo.id.value,
                    ),
                )

                null -> ResponseBody()
            }
        }
    )
}
