package me.jason5lee.post_ktor_mongo.get_user

import me.jason5lee.post_ktor_mongo.common.Time
import me.jason5lee.post_ktor_mongo.common.UserId
import me.jason5lee.post_ktor_mongo.common.UserName

typealias Query = UserId

data class User(
    val name: UserName,
    val creationTime: Time,
)

abstract class Workflow : Failures {
    abstract suspend fun run(input: Query): User
}

interface Failures {
    fun userNotFound(): Exception
}
