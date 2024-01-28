package me.jason5lee.post_ktor_mongo.edit_post

import me.jason5lee.post_ktor_mongo.common.PostContent
import me.jason5lee.post_ktor_mongo.common.PostId
import me.jason5lee.post_ktor_mongo.common.UserId
import me.jason5lee.resukt.Result
import me.jason5lee.resukt.andThen

data class Command(
    val id: PostId,
    val newContent: PostContent,
)

abstract class Workflow {
    suspend fun run(caller: UserId, input: Command): Result<Unit, Errors> =
        checkUserIsCreatorAndContentHasTheSameType(input.id, caller, input.newContent).andThen {
            updatePost(input.id, input.newContent)
        }

    abstract suspend fun checkUserIsCreatorAndContentHasTheSameType(
        post: PostId,
        user: UserId,
        content: PostContent
    ): Result<Unit, Errors>

    abstract suspend fun updatePost(post: PostId, newContent: PostContent): Result<Unit, Errors>
}

enum class Errors {
    PostNotFound,
    NotCreator,
    TypeDiff,
}
