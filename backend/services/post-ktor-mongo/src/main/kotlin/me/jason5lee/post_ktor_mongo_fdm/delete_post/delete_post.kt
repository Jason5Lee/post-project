package me.jason5lee.post_ktor_mongo.delete_post

import me.jason5lee.post_ktor_mongo.common.Identity
import me.jason5lee.post_ktor_mongo.common.PostId
import me.jason5lee.post_ktor_mongo.common.UserId

typealias Command = PostId

abstract class Workflow : Errors {
    suspend fun run(caller: Identity, input: Command) {
        val auth = (caller is Identity.Admin) || (caller is Identity.User && caller.id == getPostCreator(input))
        if (!auth) {
            throw notCreatorAdmin()
        }
        deletePost(input)
    }

    abstract suspend fun getPostCreator(post: PostId): UserId
    abstract suspend fun deletePost(post: PostId)
}

interface Errors {
    fun notCreatorAdmin(): Exception
    fun postNotFound(): Exception
}