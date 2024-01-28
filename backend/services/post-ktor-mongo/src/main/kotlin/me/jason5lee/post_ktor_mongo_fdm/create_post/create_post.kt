package me.jason5lee.post_ktor_mongo.create_post

import me.jason5lee.post_ktor_mongo.common.PostContent
import me.jason5lee.post_ktor_mongo.common.PostId
import me.jason5lee.post_ktor_mongo.common.Title
import me.jason5lee.post_ktor_mongo.common.UserId

data class Command(
    val title: Title,
    val content: PostContent,
)

abstract class Workflow : Errors {
    abstract suspend fun run(caller: UserId, input: Command): PostId
}

interface Errors {
    fun duplicateTitle(): Exception
}
