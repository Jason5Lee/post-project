package me.jason5lee.post_ktor_mongo_fdm.get_identity

import me.jason5lee.post_ktor_mongo_fdm.common.Identity
import me.jason5lee.post_ktor_mongo_fdm.common.UserId
import me.jason5lee.post_ktor_mongo_fdm.common.UserName

sealed class IdentityInfo {
    data class User(val id: UserId, val name: UserName) : IdentityInfo()
    object Admin : IdentityInfo()
}

abstract class Workflow {
    suspend fun run(caller: Identity?): IdentityInfo? = when (caller) {
        is Identity.Admin -> IdentityInfo.Admin
        is Identity.User -> IdentityInfo.User(caller.id, getUserName(caller.id))
        null -> null
    }

    abstract suspend fun getUserName(id: UserId): UserName
}
