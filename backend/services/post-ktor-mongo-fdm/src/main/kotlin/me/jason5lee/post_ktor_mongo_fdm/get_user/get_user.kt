package me.jason5lee.post_ktor_mongo_fdm.get_user

import me.jason5lee.post_ktor_mongo_fdm.common.Time
import me.jason5lee.post_ktor_mongo_fdm.common.UserId
import me.jason5lee.post_ktor_mongo_fdm.common.UserName

typealias Query = UserId

data class User(
    val name: UserName,
    val creationTime: Time,
)

abstract class Workflow : Errors {
    abstract suspend fun run(input: Query): User
}

interface Errors {
    fun userNotFound(): Exception
}
