package me.jason5lee.post_ktor_mongo.get_user

import me.jason5lee.post_ktor_mongo.common.Time
import me.jason5lee.post_ktor_mongo.common.UserName
import me.jason5lee.post_ktor_mongo.common.utils.Db
import me.jason5lee.post_ktor_mongo.common.utils.Deps

class WorkflowImpl(private val deps: Deps) : Workflow(), FailuresImpl {
    override suspend fun run(input: Query): User {
        val doc = Db.findById(
            db = deps.mongoDb,
            collection = Db.users,
            id = Db.toObjectIdOrNull(input.value) ?: throw userNotFound(),
            projection = null,
        ) ?: throw userNotFound()
        return User(
            name = UserName(doc.get("name")),
            creationTime = Time(doc.get("creationTime"))
        )
    }
}
