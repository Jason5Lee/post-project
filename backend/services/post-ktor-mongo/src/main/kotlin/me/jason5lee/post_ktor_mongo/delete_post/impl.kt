package me.jason5lee.post_ktor_mongo.delete_post

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import kotlinx.coroutines.reactive.awaitFirstOrNull
import me.jason5lee.post_ktor_mongo.common.PostId
import me.jason5lee.post_ktor_mongo.common.UserId
import me.jason5lee.post_ktor_mongo.common.utils.Db
import me.jason5lee.post_ktor_mongo.common.utils.Deps

class WorkflowImpl(private val deps: Deps) : Workflow(), FailuresImpl {
    override suspend fun getPostCreator(post: PostId): UserId {
        val doc = Db.findById(
            db = deps.mongoDb,
            collection = Db.posts,
            id = Db.toObjectIdOrNull(post.value) ?: throw postNotFound(),
            projection = Projections.include("creator")
        ) ?: throw postNotFound()
        return UserId(Db.formatId(doc.get("creator")))
    }

    override suspend fun deletePost(post: PostId) {
        deps.mongoDb.getCollection(Db.posts)
            .deleteOne(Filters.eq("_id", Db.toObjectIdOrNull(post.value) ?: throw postNotFound()))
            .awaitFirstOrNull()
    }
}
