package me.jason5lee.post_kt_vertx_fdm.admin_login

import com.mongodb.client.model.Projections
import me.jason5lee.post_kt_vertx_fdm.common.AdminId
import me.jason5lee.post_kt_vertx_fdm.common.Password
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps

class WorkflowImpl(private val deps: Deps) : me.jason5lee.post_kt_vertx_fdm.admin_login.Workflow(),
    me.jason5lee.post_kt_vertx_fdm.admin_login.ErrorsImpl {
    override suspend fun verifyPassword(id: me.jason5lee.post_kt_vertx_fdm.common.AdminId, password: me.jason5lee.post_kt_vertx_fdm.common.Password) {
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
