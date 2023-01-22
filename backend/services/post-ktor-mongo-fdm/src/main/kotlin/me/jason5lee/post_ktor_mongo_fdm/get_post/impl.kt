package me.jason5lee.post_ktor_mongo_fdm.get_post

import com.mongodb.client.model.Projections
import me.jason5lee.post_ktor_mongo_fdm.common.*
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps
import me.jason5lee.resukt.Result
import me.jason5lee.resukt.ext.asSuccess
import org.bson.types.ObjectId

class WorkflowImpl(private val deps: Deps) : Workflow() {
    override suspend fun run(id: PostId): Result<Post, Errors> {
        val objId = Db.toObjectIdOrNull(id.value) ?: return Result.failure(Errors.PostNotFound)
        val doc = Db.findById(
            db = deps.mongoDb,
            collection = Db.posts,
            id = objId,
            projection = null,
        ) ?: return Result.failure(Errors.PostNotFound)

        val title = doc.validate("title", Title::validate)
        val content = run {
            val text = doc.validateOptional("text", TextPostContent::validate)
            val url = doc.validateOptional("url", UrlPostContent::validate)

            if (text != null && url == null) {
                PostContent.Text(text)
            } else if (text == null && url != null) {
                PostContent.Url(url)
            } else {
                throw Exception("Invalid query result from `${Db.posts}[_id = $objId]`, only exact one of `text` and `url` must present")
            }
        }
        val creator = doc.get<ObjectId>("creator")
        val creationTime = doc.validate("creationTime", Time::validate)
        val lastModified = doc.validateOptional("lastModified", Time::validate)

        val creatorDoc = Db.findById(
            db = deps.mongoDb,
            collection = Db.users,
            id = creator,
            projection = Projections.include("name"),
        ) ?: throw Exception("Invalid query result from `${Db.posts}[_id = $objId].creator`, not found in `${Db.users}`")
        val creatorName = creatorDoc.validate("name", UserName::validate)

        return Post(
            creator = Creator(
                id = UserId(Db.formatId(creator)),
                name = creatorName,
            ),
            creationTime = creationTime,
            lastModified = lastModified,
            title = title,
            content = content,
        ).asSuccess()
    }
}
