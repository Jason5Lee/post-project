package me.jason5lee.post_kt_vertx_fdm.admin_login

import me.jason5lee.post_kt_vertx_fdm.common.AdminId
import me.jason5lee.post_kt_vertx_fdm.common.Password

data class Query(
    val id: me.jason5lee.post_kt_vertx_fdm.common.AdminId,
    val password: me.jason5lee.post_kt_vertx_fdm.common.Password,
)

abstract class Workflow : me.jason5lee.post_kt_vertx_fdm.admin_login.Errors {
    suspend fun run(input: me.jason5lee.post_kt_vertx_fdm.admin_login.Query): me.jason5lee.post_kt_vertx_fdm.common.AdminId {
        verifyPassword(input.id, input.password)
        return input.id
    }

    abstract suspend fun verifyPassword(id: me.jason5lee.post_kt_vertx_fdm.common.AdminId, password: me.jason5lee.post_kt_vertx_fdm.common.Password)
}

interface Errors {
    fun idOrPasswordIncorrect(): Exception
}
