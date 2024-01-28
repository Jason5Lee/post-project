package me.jason5lee.post_ktor_mongo.user_register

import me.jason5lee.post_ktor_mongo.common.Password
import me.jason5lee.post_ktor_mongo.common.UserId
import me.jason5lee.post_ktor_mongo.common.UserName

data class Command(
    val userName: UserName,
    val password: Password,
)

abstract class Workflow {
    suspend fun run(input: Command): UserId = createUser(input.userName, input.password)

    abstract suspend fun createUser(userName: UserName, password: Password): UserId
}

interface Errors {
    fun userNameAlreadyExists(): Exception
}
