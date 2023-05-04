package me.jason5lee.post_kt_vertx_fdm.edit_post

import com.mongodb.client.model.Projections
import kotlinx.coroutines.reactive.awaitSingle
import me.jason5lee.post_kt_vertx_fdm.common.PostContent
import me.jason5lee.post_kt_vertx_fdm.common.PostId
import me.jason5lee.post_kt_vertx_fdm.common.UserId
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps
import me.jason5lee.post_ktor_mongo_fdm.common.utils.timeFromNow
import me.jason5lee.resukt.Result
import org.bson.Document
import org.bson.types.ObjectId

class WorkflowImpl(private val deps: Deps) : Workflow() {
    override suspend fun checkUserIsCreatorAndContentHasTheSameType(
        post: me.jason5lee.post_kt_vertx_fdm.common.PostId,
        user: me.jason5lee.post_kt_vertx_fdm.common.UserId,
        content: me.jason5lee.post_kt_vertx_fdm.common.PostContent
    ): Result<Unit, Errors> {
        val postDoc = Db.findById(
            db = deps.mongoDb,
            collection = Db.posts,
            id = Db.toObjectIdOrNull(post.value) ?: return Result.failure(Errors.PostNotFound),
            projection = Projections.include("creator", "text", "url")
        ) ?: return Result.failure(Errors.PostNotFound)
        if (postDoc.get<ObjectId>("creator") != Db.toObjectIdOrNull(user.value)) {
            return Result.failure(Errors.NotCreator)
        }
        val sameType = when (content) {
            is me.jason5lee.post_kt_vertx_fdm.common.PostContent.Text -> postDoc.doc.containsKey("text")
            is me.jason5lee.post_kt_vertx_fdm.common.PostContent.Url -> postDoc.doc.containsKey("url")
        }
        if (!sameType) {
            return Result.failure(Errors.TypeDiff)
        }
        return Result.success(Unit)
    }

    override suspend fun updatePost(post: me.jason5lee.post_kt_vertx_fdm.common.PostId, newContent: me.jason5lee.post_kt_vertx_fdm.common.PostContent): Result<Unit, Errors> {
        val lastModified = timeFromNow(offset = 0).utc
        val updateSet = when (newContent) {
            is me.jason5lee.post_kt_vertx_fdm.common.PostContent.Text -> Document("text", newContent.value.value)
            is me.jason5lee.post_kt_vertx_fdm.common.PostContent.Url -> Document("url", newContent.value.value)
        }
        updateSet.append("lastModified", lastModified)
        deps.mongoDb.getCollection(Db.posts).updateOne(
            Document("_id", Db.toObjectIdOrNull(post.value) ?: return Result.failure(Errors.PostNotFound)),
            Document("\$set", updateSet)
        ).awaitSingle()
        return Result.success(Unit)
    }
}
