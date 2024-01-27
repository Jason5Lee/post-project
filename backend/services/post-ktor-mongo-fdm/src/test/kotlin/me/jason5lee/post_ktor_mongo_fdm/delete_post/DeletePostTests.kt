package me.jason5lee.post_ktor_mongo_fdm.delete_post

import kotlinx.coroutines.runBlocking
import me.jason5lee.post_ktor_mongo_fdm.common.Identity
import me.jason5lee.post_ktor_mongo_fdm.common.PostId
import me.jason5lee.post_ktor_mongo_fdm.common.UserId
import me.jason5lee.post_ktor_mongo_fdm.common.utils.ExpectedException
import kotlin.test.Test

class DeletePostTests {
    @Test
    fun `fail if the caller is a user but not the creator`() {
        val mockWorkflow = object : Workflow() {
            override suspend fun getPostCreator(post: PostId): UserId = UserId("0")

            override suspend fun deletePost(post: PostId) {
                throw Exception("Should not be called")
            }

            override fun notCreatorAdmin(): Exception = ExpectedException()

            override fun postNotFound(): Exception {
                throw Exception("Should not be called")
            }
        }
        try {
            runBlocking { mockWorkflow.run(Identity.User(UserId("1")), PostId("0")) }
            throw Exception("Should not be reached")
        } catch (_: ExpectedException) {
        }
    }

    @Test
    fun `success if the caller is the creator`() {
        val mockWorkflow = object : Workflow() {
            override suspend fun getPostCreator(post: PostId): UserId = UserId("1")

            override suspend fun deletePost(post: PostId) {}

            override fun notCreatorAdmin(): Exception {
                throw Exception("Should not be called")
            }

            override fun postNotFound(): Exception {
                throw Exception("Should not be called")
            }
        }
        runBlocking { mockWorkflow.run(Identity.User(UserId("1")), PostId("0")) }
    }

    @Test
    fun `success if the caller is the admin`() {
        val mockWorkflow = object : Workflow() {
            override suspend fun getPostCreator(post: PostId): UserId = UserId("0")

            override suspend fun deletePost(post: PostId) {}

            override fun notCreatorAdmin(): Exception {
                throw Exception("Should not be called")
            }

            override fun postNotFound(): Exception {
                throw Exception("Should not be called")
            }
        }
        runBlocking { mockWorkflow.run(Identity.Admin, PostId("0")) }
    }
}
