package me.jason5lee.post_ktor_mongo.user_login

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import me.jason5lee.post_ktor_mongo.common.Password
import me.jason5lee.post_ktor_mongo.common.UserId
import me.jason5lee.post_ktor_mongo.common.UserName
import me.jason5lee.post_ktor_mongo.common.utils.Db
import me.jason5lee.post_ktor_mongo.common.utils.Deps
import org.bson.types.ObjectId

class WorkflowImpl(private val deps: Deps) : Workflow(), FailuresImpl {
    override suspend fun verifyPasswordAndGetUserId(userName: UserName, password: Password): UserId {
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
        return UserId(Db.formatId(userDoc.get("_id")))
    }
}
