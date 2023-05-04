package me.jason5lee.post_kt_vertx_fdm.delete_post

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import kotlinx.coroutines.reactive.awaitFirstOrNull
import me.jason5lee.post_kt_vertx_fdm.common.PostId
import me.jason5lee.post_kt_vertx_fdm.common.UserId
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps

class WorkflowImpl(private val deps: Deps) : Workflow(), ErrorsImpl {
    override suspend fun getPostCreator(post: me.jason5lee.post_kt_vertx_fdm.common.PostId): me.jason5lee.post_kt_vertx_fdm.common.UserId {
        val doc = Db.findById(
            db = deps.mongoDb,
            collection = Db.posts,
            id = Db.toObjectIdOrNull(post.value) ?: throw postNotFound(),
            projection = Projections.include("creator")
        ) ?: throw postNotFound()
        return me.jason5lee.post_kt_vertx_fdm.common.UserId(Db.formatId(doc.get("creator")))
    }

    override suspend fun deletePost(post: me.jason5lee.post_kt_vertx_fdm.common.PostId) {
        deps.mongoDb.getCollection(Db.posts)
            .deleteOne(Filters.eq("_id", Db.toObjectIdOrNull(post.value) ?: throw postNotFound()))
            .awaitFirstOrNull()
    }
}
