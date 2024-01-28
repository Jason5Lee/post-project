package me.jason5lee.post_ktor_mongo.edit_post

import kotlinx.coroutines.runBlocking
import me.jason5lee.post_ktor_mongo.common.PostContent
import me.jason5lee.post_ktor_mongo.common.PostId
import me.jason5lee.post_ktor_mongo.common.TextPostContent
import me.jason5lee.post_ktor_mongo.common.UserId
import me.jason5lee.resukt.Result
import kotlin.test.Test
import kotlin.test.assertEquals

class EditPostTests {
    companion object {
        private val testPostContent = PostContent.Text(TextPostContent("ccc"))
    }

    @Test
    fun `fail if the caller is not the creator`() {
        val mockWorkflow = object : Workflow() {
            override suspend fun checkUserIsCreatorAndContentHasTheSameType(
                post: PostId,
                user: UserId,
                content: PostContent
            ): Result<Unit, Failures> = Result.failure(Failures.NotCreator)

            override suspend fun updatePost(post: PostId, newContent: PostContent): Result<Unit, Failures> {
                throw Exception("Should not be reached")
            }
        }

        val result = runBlocking { mockWorkflow.run(UserId("0"), Command(PostId("0"), testPostContent)) }
        assertEquals(Result.failure(Failures.NotCreator), result)
    }

    @Test
    fun `fail if the type is different`() {
        val mockWorkflow = object : Workflow() {
            override suspend fun checkUserIsCreatorAndContentHasTheSameType(
                post: PostId,
                user: UserId,
                content: PostContent
            ): Result<Unit, Failures> = Result.failure(Failures.TypeDiff)

            override suspend fun updatePost(post: PostId, newContent: PostContent): Result<Unit, Failures> {
                throw Exception("Should not be reached")
            }
        }

        val result = runBlocking { mockWorkflow.run(UserId("0"), Command(PostId("0"), testPostContent)) }
        assertEquals(Result.failure(Failures.TypeDiff), result)
    }

    @Test
    fun `test if the post not found`() {
        val mockWorkflow = object : Workflow() {
            override suspend fun checkUserIsCreatorAndContentHasTheSameType(
                post: PostId,
                user: UserId,
                content: PostContent
            ): Result<Unit, Failures> = Result.failure(Failures.PostNotFound)

            override suspend fun updatePost(post: PostId, newContent: PostContent): Result<Unit, Failures> {
                throw Exception("Should not be reached")
            }
        }

        val result = runBlocking { mockWorkflow.run(UserId("0"), Command(PostId("0"), testPostContent)) }
        assertEquals(Result.failure(Failures.PostNotFound), result)
    }

    @Test
    fun `test if the post is deleted`() {
        val mockWorkflow = object : Workflow() {
            override suspend fun checkUserIsCreatorAndContentHasTheSameType(
                post: PostId,
                user: UserId,
                content: PostContent
            ): Result<Unit, Failures> = Result.success(Unit)

            override suspend fun updatePost(post: PostId, newContent: PostContent): Result<Unit, Failures> =
                Result.success(Unit)
        }

        val result = runBlocking { mockWorkflow.run(UserId("0"), Command(PostId("0"), testPostContent)) }
        assertEquals(Result.success(Unit), result)
    }
}
