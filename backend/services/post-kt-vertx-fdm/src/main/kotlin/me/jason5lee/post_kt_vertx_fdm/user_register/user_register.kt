package me.jason5lee.post_kt_vertx_fdm.user_register

import me.jason5lee.post_kt_vertx_fdm.common.Password
import me.jason5lee.post_kt_vertx_fdm.common.UserId
import me.jason5lee.post_kt_vertx_fdm.common.UserName

data class Command(
    val userName: me.jason5lee.post_kt_vertx_fdm.common.UserName,
    val password: me.jason5lee.post_kt_vertx_fdm.common.Password,
)

abstract class Workflow {
    suspend fun run(input: Command): me.jason5lee.post_kt_vertx_fdm.common.UserId = createUser(input.userName, input.password)

    abstract suspend fun createUser(userName: me.jason5lee.post_kt_vertx_fdm.common.UserName, password: me.jason5lee.post_kt_vertx_fdm.common.Password): me.jason5lee.post_kt_vertx_fdm.common.UserId
}

interface Errors {
    fun userNameAlreadyExists(): Exception
}
