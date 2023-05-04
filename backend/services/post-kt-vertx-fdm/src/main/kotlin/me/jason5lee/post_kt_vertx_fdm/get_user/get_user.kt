package me.jason5lee.post_kt_vertx_fdm.get_user

import me.jason5lee.post_kt_vertx_fdm.common.Time
import me.jason5lee.post_kt_vertx_fdm.common.UserId
import me.jason5lee.post_kt_vertx_fdm.common.UserName

typealias Query = me.jason5lee.post_kt_vertx_fdm.common.UserId

data class User(
    val name: me.jason5lee.post_kt_vertx_fdm.common.UserName,
    val creationTime: me.jason5lee.post_kt_vertx_fdm.common.Time,
)

abstract class Workflow : Errors {
    abstract suspend fun run(input: Query): User
}

interface Errors {
    fun userNotFound(): Exception
}
