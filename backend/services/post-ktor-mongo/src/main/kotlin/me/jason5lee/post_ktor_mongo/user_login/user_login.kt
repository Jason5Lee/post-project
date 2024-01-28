package me.jason5lee.post_ktor_mongo.user_login

import me.jason5lee.post_ktor_mongo.common.Password
import me.jason5lee.post_ktor_mongo.common.UserId
import me.jason5lee.post_ktor_mongo.common.UserName

data class Query(
    val userName: UserName,
    val password: Password,
)

abstract class Workflow : Failures {
    suspend fun run(input: Query): UserId = verifyPasswordAndGetUserId(input.userName, input.password)

    abstract suspend fun verifyPasswordAndGetUserId(userName: UserName, password: Password): UserId
}

interface Failures {
    fun userNameOrPasswordIncorrect(): Exception
}
