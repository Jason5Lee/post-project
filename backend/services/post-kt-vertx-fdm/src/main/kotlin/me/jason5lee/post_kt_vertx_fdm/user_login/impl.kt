package me.jason5lee.post_kt_vertx_fdm.user_login

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import me.jason5lee.post_kt_vertx_fdm.common.Password
import me.jason5lee.post_kt_vertx_fdm.common.UserId
import me.jason5lee.post_kt_vertx_fdm.common.UserName
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps
import org.bson.types.ObjectId

class WorkflowImpl(private val deps: Deps) : Workflow(), ErrorsImpl {
    override suspend fun verifyPasswordAndGetUserId(userName: me.jason5lee.post_kt_vertx_fdm.common.UserName, password: me.jason5lee.post_kt_vertx_fdm.common.Password): me.jason5lee.post_kt_vertx_fdm.common.UserId {
        val userDoc = Db.findOne<ObjectId>(
            db = deps.mongoDb,
            collection = Db.users,
            configCollection = {
                this.find(Filters.eq("name", userName.value))
                    .projection(Projections.include("encryptedPassword", "_id"))
            }
        ) ?: throw userNameOrPasswordIncorrect()
        val encryptedPassword = userDoc.get<String>("encryptedPassword")

        if (!password.verify(Db.BCrypt.Verifier(encryptedPassword))) {
            throw userNameOrPasswordIncorrect()
        }
        return me.jason5lee.post_kt_vertx_fdm.common.UserId(Db.formatId(userDoc.get("_id")))
    }
}
