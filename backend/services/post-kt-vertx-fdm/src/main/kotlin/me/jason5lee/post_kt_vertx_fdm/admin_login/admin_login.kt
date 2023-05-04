package me.jason5lee.post_kt_vertx_fdm.admin_login

import me.jason5lee.post_kt_vertx_fdm.common.AdminId
import me.jason5lee.post_kt_vertx_fdm.common.Password

data class Query(
    val id: AdminId,
    val password: Password,
)

abstract class Workflow : Failures {
    suspend fun run(input: Query): AdminId {
        verifyPassword(input.id, input.password)
        return input.id
    }

    abstract suspend fun verifyPassword(id: AdminId, password: Password)
}

interface Failures {
    fun idOrPasswordIncorrect(): Exception
}
