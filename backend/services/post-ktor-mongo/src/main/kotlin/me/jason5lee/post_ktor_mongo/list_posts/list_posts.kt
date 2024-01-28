package me.jason5lee.post_ktor_mongo.list_posts

import me.jason5lee.post_ktor_mongo.common.*

data class Query(
    val page: Page,
    val pageSize: PageSize,
    val creator: UserId?,
)

data class Post(
    val id: PostId,
    val title: Title,
    val creator: Creator,
    val creationTime: Time,
)

data class Creator(
    val id: UserId,
    val name: UserName,
)

data class Output(
    val total: Long,
    val posts: List<Post>,
)

abstract class Workflow : Failures {
    abstract suspend fun run(input: Query): Output
}

interface Failures {
    fun creatorNotFound(): Exception
}
