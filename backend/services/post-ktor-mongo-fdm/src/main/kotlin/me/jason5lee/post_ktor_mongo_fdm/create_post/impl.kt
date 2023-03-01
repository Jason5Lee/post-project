package me.jason5lee.post_ktor_mongo_fdm.create_post

import com.mongodb.MongoWriteException
import kotlinx.coroutines.reactive.awaitSingle
import me.jason5lee.post_ktor_mongo_fdm.common.PostContent
import me.jason5lee.post_ktor_mongo_fdm.common.PostId
import me.jason5lee.post_ktor_mongo_fdm.common.UserId
import me.jason5lee.post_ktor_mongo_fdm.common.api.invalidAuth
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps
import me.jason5lee.post_ktor_mongo_fdm.common.utils.timeFromNow
import org.bson.Document

class WorkflowImpl(val deps: Deps) : Workflow(), ErrorsImpl {
    override suspend fun run(caller: UserId, input: Command): PostId {
        val doc = Document()
        when (val content = input.content) {
            is PostContent.Text -> doc["text"] = content.value.value
            is PostContent.Url -> doc["url"] = content.value.value
        }
        doc["creator"] = Db.toObjectIdOrNull(caller.value) ?: throw invalidAuth()
        doc["creationTime"] = timeFromNow(offset = 0).utc
        doc["title"] = input.title.value

        val result = try {
            deps.mongoDb.getCollection(Db.posts).insertOne(doc).awaitSingle()
        } catch (e: MongoWriteException) { // I was also surprised that this is not DuplicateKeyException.
            throw duplicateTitle()
        }
        return PostId(Db.formatId(result.insertedId!!.asObjectId().value))
    }
}
