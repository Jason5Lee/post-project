package me.jason5lee.post_kt_vertx_fdm.create_post

import com.mongodb.MongoWriteException
import kotlinx.coroutines.reactive.awaitSingle
import me.jason5lee.post_kt_vertx_fdm.common.PostContent
import me.jason5lee.post_kt_vertx_fdm.common.PostId
import me.jason5lee.post_kt_vertx_fdm.common.UserId
import me.jason5lee.post_kt_vertx_fdm.common.api.invalidAuth
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps
import me.jason5lee.post_ktor_mongo_fdm.common.utils.timeFromNow
import org.bson.Document

class WorkflowImpl(val deps: Deps) : Workflow(), ErrorsImpl {
    override suspend fun run(caller: me.jason5lee.post_kt_vertx_fdm.common.UserId, input: Command): me.jason5lee.post_kt_vertx_fdm.common.PostId {
        val doc = Document()
        when (val content = input.content) {
            is me.jason5lee.post_kt_vertx_fdm.common.PostContent.Text -> doc["text"] = content.value.value
            is me.jason5lee.post_kt_vertx_fdm.common.PostContent.Url -> doc["url"] = content.value.value
        }
        doc["creator"] = Db.toObjectIdOrNull(caller.value) ?: throw me.jason5lee.post_kt_vertx_fdm.common.api.invalidAuth()
        doc["creationTime"] = timeFromNow(offset = 0).utc
        doc["title"] = input.title.value

        val result = try {
            deps.mongoDb.getCollection(Db.posts).insertOne(doc).awaitSingle()
        } catch (e: MongoWriteException) { // I was also surprised that this is not DuplicateKeyException.
            throw duplicateTitle()
        }
        return me.jason5lee.post_kt_vertx_fdm.common.PostId(Db.formatId(result.insertedId!!.asObjectId().value))
    }
}
