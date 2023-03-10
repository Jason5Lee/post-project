package me.jason5lee.post_ktor_mongo_fdm.get_user

import me.jason5lee.post_ktor_mongo_fdm.common.Time
import me.jason5lee.post_ktor_mongo_fdm.common.UserName
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps

class WorkflowImpl(private val deps: Deps) : Workflow(), ErrorsImpl {
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
