package me.jason5lee.post_kt_vertx_fdm.get_post

import com.mongodb.client.model.Projections
import me.jason5lee.post_ktor_mongo_fdm.common.*
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps
import me.jason5lee.resukt.Result
import me.jason5lee.resukt.ext.asSuccess
import org.bson.types.ObjectId

class WorkflowImpl(private val deps: Deps) : Workflow() {
    override suspend fun run(id: me.jason5lee.post_kt_vertx_fdm.common.PostId): Result<Post, Errors> {
        val objId = Db.toObjectIdOrNull(id.value) ?: return Result.failure(Errors.PostNotFound)
        val doc = Db.findById(
            db = deps.mongoDb,
            collection = Db.posts,
            id = objId,
            projection = null,
        ) ?: return Result.failure(Errors.PostNotFound)

        val title = me.jason5lee.post_kt_vertx_fdm.common.Title(doc.get("title"))
        val content = run {
            val text = doc.getOptional<String>("text")?.let { me.jason5lee.post_kt_vertx_fdm.common.TextPostContent(it) }
            val url = doc.getOptional<String>("url")?.let { me.jason5lee.post_kt_vertx_fdm.common.UrlPostContent(it) }

            if (text != null && url == null) {
                me.jason5lee.post_kt_vertx_fdm.common.PostContent.Text(text)
            } else if (text == null && url != null) {
                me.jason5lee.post_kt_vertx_fdm.common.PostContent.Url(url)
            } else {
                throw Exception("Invalid query result from `${Db.posts}[_id = $objId]`, only exact one of `text` and `url` must present")
            }
        }
        val creator = doc.get<ObjectId>("creator")
        val creationTime = me.jason5lee.post_kt_vertx_fdm.common.Time(doc.get("creationTime"))
        val lastModified = doc.getOptional<Long>("lastModified")?.let { me.jason5lee.post_kt_vertx_fdm.common.Time(it) }

        val creatorDoc = Db.findById(
            db = deps.mongoDb,
            collection = Db.users,
            id = creator,
            projection = Projections.include("name"),
        )
            ?: throw Exception("Invalid query result from `${Db.posts}[_id = $objId].creator`, not found in `${Db.users}`")
        val creatorName = me.jason5lee.post_kt_vertx_fdm.common.UserName(creatorDoc.get("name"))

        return Post(
            creator = Creator(
                id = me.jason5lee.post_kt_vertx_fdm.common.UserId(Db.formatId(creator)),
                name = creatorName,
            ),
            creationTime = creationTime,
            lastModified = lastModified,
            title = title,
            content = content,
        ).asSuccess()
    }
}
