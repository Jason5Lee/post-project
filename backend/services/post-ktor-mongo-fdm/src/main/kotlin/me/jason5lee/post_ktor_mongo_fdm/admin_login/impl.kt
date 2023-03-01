package me.jason5lee.post_ktor_mongo_fdm.admin_login

import com.mongodb.client.model.Projections
import me.jason5lee.post_ktor_mongo_fdm.common.AdminId
import me.jason5lee.post_ktor_mongo_fdm.common.Password
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps

class WorkflowImpl(private val deps: Deps) : Workflow(), ErrorsImpl {
    override suspend fun verifyPassword(id: AdminId, password: Password) {
        val admin = Db.findById(
            db = deps.mongoDb,
            collection = Db.admins,
            id = Db.toObjectIdOrNull(id.value) ?: throw idOrPasswordIncorrect(),
            projection = Projections.include("encryptedPassword")
        ) ?: throw idOrPasswordIncorrect()
        val encryptedPassword = admin.get<String>("encryptedPassword")
        if (!password.verify(Db.BCrypt.Verifier(encryptedPassword))) {
            throw idOrPasswordIncorrect()
        }
    }
}
