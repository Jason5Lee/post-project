package me.jason5lee.post_ktor_mongo_fdm.edit_post

import kotlinx.coroutines.runBlocking
import me.jason5lee.post_ktor_mongo_fdm.common.*
import me.jason5lee.resukt.Result
import kotlin.test.Test
import kotlin.test.assertEquals

class EditPostTests {
    companion object {
        private val testPostContent = PostContent.Text(TextPostContent.validate("ccc").assertValid())
    }

    @Test
    fun `fail if the caller is not the creator`() {
        val mockWorkflow = object : Workflow() {
            override suspend fun checkUserIsCreatorAndContentHasTheSameType(
                post: PostId,
                user: UserId,
                content: PostContent
            ): Result<Unit, Errors> = Result.failure(Errors.NotCreator)

            override suspend fun updatePost(post: PostId, newContent: PostContent): Result<Unit, Errors> {
                throw Exception("Should not be reached")
            }
        }

        val result = runBlocking { mockWorkflow.run(UserId("0"), Command(PostId("0"), testPostContent)) }
        assertEquals(Result.failure(Errors.NotCreator), result)
    }

    @Test
    fun `fail if the type is different`() {
        val mockWorkflow = object : Workflow() {
            override suspend fun checkUserIsCreatorAndContentHasTheSameType(
                post: PostId,
                user: UserId,
                content: PostContent
            ): Result<Unit, Errors> = Result.failure(Errors.TypeDiff)

            override suspend fun updatePost(post: PostId, newContent: PostContent): Result<Unit, Errors> {
                throw Exception("Should not be reached")
            }
        }

        val result = runBlocking { mockWorkflow.run(UserId("0"), Command(PostId("0"), testPostContent)) }
        assertEquals(Result.failure(Errors.TypeDiff), result)
    }

    @Test
    fun `test if the post not found`() {
        val mockWorkflow = object : Workflow() {
            override suspend fun checkUserIsCreatorAndContentHasTheSameType(
                post: PostId,
                user: UserId,
                content: PostContent
            ): Result<Unit, Errors> = Result.failure(Errors.PostNotFound)

            override suspend fun updatePost(post: PostId, newContent: PostContent): Result<Unit, Errors> {
                throw Exception("Should not be reached")
            }
        }

        val result = runBlocking { mockWorkflow.run(UserId("0"), Command(PostId("0"), testPostContent)) }
        assertEquals(Result.failure(Errors.PostNotFound), result)
    }
    @Test
    fun `test if the post is deleted`() {
        val mockWorkflow = object : Workflow() {
            override suspend fun checkUserIsCreatorAndContentHasTheSameType(
                post: PostId,
                user: UserId,
                content: PostContent
            ): Result<Unit, Errors> = Result.success(Unit)

            override suspend fun updatePost(post: PostId, newContent: PostContent): Result<Unit, Errors> =
                Result.success(Unit)
        }

        val result = runBlocking { mockWorkflow.run(UserId("0"), Command(PostId("0"), testPostContent)) }
        assertEquals(Result.success(Unit), result)
    }
}