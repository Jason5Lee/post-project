package me.jason5lee.post_ktor_mongo.get_post

import me.jason5lee.post_ktor_mongo.common.*
import me.jason5lee.resukt.Result

data class Creator(
    val id: UserId,
    val name: UserName,
)

data class Post(
    val creator: Creator,
    val creationTime: Time,
    val lastModified: Time?,
    val title: Title,
    val content: PostContent,
)

abstract class Workflow {
    abstract suspend fun run(id: PostId): Result<Post, Failures>
}

enum class Failures {
    PostNotFound,
}
