package me.jason5lee.post_ktor_mongo_fdm.list_posts

import me.jason5lee.post_ktor_mongo_fdm.common.*

sealed class Condition {
    data class Before(val time: Time) : Condition()
    data class After(val time: Time) : Condition()
}

data class Query(
    val creator: UserId?,
    val condition: Condition?,
    val size: Size,
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

abstract class Workflow: Errors {
    abstract suspend fun run(input: Query): List<Post>
}

interface Errors {
    fun creatorNotFound(): Exception
}
