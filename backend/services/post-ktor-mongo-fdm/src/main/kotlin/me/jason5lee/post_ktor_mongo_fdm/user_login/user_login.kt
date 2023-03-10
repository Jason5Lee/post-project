package me.jason5lee.post_ktor_mongo_fdm.user_login

import me.jason5lee.post_ktor_mongo_fdm.common.Password
import me.jason5lee.post_ktor_mongo_fdm.common.UserId
import me.jason5lee.post_ktor_mongo_fdm.common.UserName

data class Query(
    val userName: UserName,
    val password: Password,
)

abstract class Workflow : Errors {
    suspend fun run(input: Query): UserId = verifyPasswordAndGetUserId(input.userName, input.password)

    abstract suspend fun verifyPasswordAndGetUserId(userName: UserName, password: Password): UserId
}

interface Errors {
    fun userNameOrPasswordIncorrect(): Exception
}
