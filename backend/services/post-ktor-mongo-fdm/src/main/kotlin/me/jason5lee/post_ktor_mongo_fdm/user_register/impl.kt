package me.jason5lee.post_ktor_mongo_fdm.user_register

import com.mongodb.MongoWriteException
import kotlinx.coroutines.reactive.awaitSingle
import me.jason5lee.post_ktor_mongo_fdm.common.Password
import me.jason5lee.post_ktor_mongo_fdm.common.UserId
import me.jason5lee.post_ktor_mongo_fdm.common.UserName
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps
import me.jason5lee.post_ktor_mongo_fdm.common.utils.timeFromNow
import org.bson.Document

class WorkflowImpl(private val deps: Deps) : Workflow(), ErrorsImpl {
    override suspend fun createUser(userName: UserName, password: Password): UserId {
        val doc = Document()
        doc["name"] = userName.value
        doc["encryptedPassword"] = password.encrypt(deps.bCrypt)
        doc["creationTime"] = timeFromNow(offset = 0).utc

        val inserted = try {
            deps.mongoDb.getCollection(Db.users).insertOne(doc).awaitSingle()
        } catch (e: MongoWriteException) {
            throw userNameAlreadyExists()
        }

        return UserId(Db.formatId(inserted.insertedId!!.asObjectId().value))
    }
}
