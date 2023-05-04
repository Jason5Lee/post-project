package me.jason5lee.post_kt_vertx_fdm.user_login

import me.jason5lee.post_kt_vertx_fdm.common.Password
import me.jason5lee.post_kt_vertx_fdm.common.UserId
import me.jason5lee.post_kt_vertx_fdm.common.UserName

data class Query(
    val userName: me.jason5lee.post_kt_vertx_fdm.common.UserName,
    val password: me.jason5lee.post_kt_vertx_fdm.common.Password,
)

abstract class Workflow : Errors {
    suspend fun run(input: Query): me.jason5lee.post_kt_vertx_fdm.common.UserId = verifyPasswordAndGetUserId(input.userName, input.password)

    abstract suspend fun verifyPasswordAndGetUserId(userName: me.jason5lee.post_kt_vertx_fdm.common.UserName, password: me.jason5lee.post_kt_vertx_fdm.common.Password): me.jason5lee.post_kt_vertx_fdm.common.UserId
}

interface Errors {
    fun userNameOrPasswordIncorrect(): Exception
}
