package me.jason5lee.post_ktor_mongo_fdm.admin_login

import me.jason5lee.post_ktor_mongo_fdm.common.AdminId
import me.jason5lee.post_ktor_mongo_fdm.common.Password

data class Query(
    val id: AdminId,
    val password: Password,
)

abstract class Workflow : Errors {
    suspend fun run(input: Query): AdminId {
        verifyPassword(input.id, input.password)
        return input.id
    }

    abstract suspend fun verifyPassword(id: AdminId, password: Password)
}

interface Errors {
    fun idOrPasswordIncorrect(): Exception
}
