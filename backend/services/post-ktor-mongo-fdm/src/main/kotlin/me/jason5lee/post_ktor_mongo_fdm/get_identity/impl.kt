package me.jason5lee.post_ktor_mongo_fdm.get_identity

import com.mongodb.client.model.Projections
import me.jason5lee.post_ktor_mongo_fdm.common.UserId
import me.jason5lee.post_ktor_mongo_fdm.common.UserName
import me.jason5lee.post_ktor_mongo_fdm.common.api.invalidAuth
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps

class WorkflowImpl(private val deps: Deps) : Workflow() {
    override suspend fun getUserName(id: UserId): UserName {
        val doc = Db.findById(
            db = deps.mongoDb,
            collection = Db.users,
            id = Db.toObjectIdOrNull(id.value) ?: throw invalidAuth(),
            projection = Projections.include("name")
        ) ?: throw invalidAuth()
        return UserName(doc.get("name"))
    }
}
