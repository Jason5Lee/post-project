package me.jason5lee.post_ktor_mongo_fdm.edit_post

import com.mongodb.client.model.Projections
import kotlinx.coroutines.reactive.awaitSingle
import me.jason5lee.post_ktor_mongo_fdm.common.PostContent
import me.jason5lee.post_ktor_mongo_fdm.common.PostId
import me.jason5lee.post_ktor_mongo_fdm.common.UserId
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps
import me.jason5lee.post_ktor_mongo_fdm.common.utils.timeFromNow
import me.jason5lee.resukt.Result
import org.bson.Document
import org.bson.types.ObjectId

class WorkflowImpl(private val deps: Deps) : Workflow() {
    override suspend fun checkUserIsCreatorAndContentHasTheSameType(
        post: PostId,
        user: UserId,
        content: PostContent
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
            is PostContent.Text -> postDoc.doc.containsKey("text")
            is PostContent.Url -> postDoc.doc.containsKey("url")
        }
        if (!sameType) {
            return Result.failure(Errors.TypeDiff)
        }
        return Result.success(Unit)
    }

    override suspend fun updatePost(post: PostId, newContent: PostContent): Result<Unit, Errors> {
        val lastModified = timeFromNow(offset = 0).utc
        val updateSet = when (newContent) {
            is PostContent.Text -> Document("text", newContent.value.value)
            is PostContent.Url -> Document("url", newContent.value.value)
        }
        updateSet.append("lastModified", lastModified)
        deps.mongoDb.getCollection(Db.posts).updateOne(
            Document("_id", Db.toObjectIdOrNull(post.value) ?: return Result.failure(Errors.PostNotFound)),
            Document("\$set", updateSet)
        ).awaitSingle()
        return Result.success(Unit)
    }
}
