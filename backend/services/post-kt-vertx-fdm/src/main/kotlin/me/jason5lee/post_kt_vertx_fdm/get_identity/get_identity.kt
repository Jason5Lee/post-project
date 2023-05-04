package me.jason5lee.post_kt_vertx_fdm.get_identity

import me.jason5lee.post_kt_vertx_fdm.common.AdminId
import me.jason5lee.post_kt_vertx_fdm.common.Identity
import me.jason5lee.post_kt_vertx_fdm.common.UserId
import me.jason5lee.post_kt_vertx_fdm.common.UserName

sealed class IdentityInfo {
    data class User(val id: me.jason5lee.post_kt_vertx_fdm.common.UserId, val name: me.jason5lee.post_kt_vertx_fdm.common.UserName) : IdentityInfo()
    data class Admin(val id: me.jason5lee.post_kt_vertx_fdm.common.AdminId) : IdentityInfo()
}

abstract class Workflow {
    suspend fun run(caller: me.jason5lee.post_kt_vertx_fdm.common.Identity?): IdentityInfo? = when (caller) {
        is me.jason5lee.post_kt_vertx_fdm.common.Identity.Admin -> IdentityInfo.Admin(caller.id)
        is me.jason5lee.post_kt_vertx_fdm.common.Identity.User -> IdentityInfo.User(caller.id, getUserName(caller.id))
        null -> null
    }

    abstract suspend fun getUserName(id: me.jason5lee.post_kt_vertx_fdm.common.UserId): me.jason5lee.post_kt_vertx_fdm.common.UserName
}
