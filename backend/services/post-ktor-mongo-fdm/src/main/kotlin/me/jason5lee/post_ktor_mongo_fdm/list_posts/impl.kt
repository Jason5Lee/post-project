package me.jason5lee.post_ktor_mongo_fdm.list_posts

import com.mongodb.client.model.Projections
import kotlinx.coroutines.flow.toList
import me.jason5lee.post_ktor_mongo_fdm.common.*
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps
import org.bson.Document
import org.bson.types.ObjectId

class WorkflowImpl(private val deps: Deps) : Workflow(), ErrorsImpl {
    override suspend fun run(input: Query): List<Post> {
        val filter = Document()
        val creatorMap: MutableMap<ObjectId, UserName> = mutableMapOf()
        if (input.creator != null) {
            val creatorOid = Db.toObjectIdOrNull(input.creator.value) ?: throw creatorNotFound()
            val creatorUserDoc = Db.findById(
                db = deps.mongoDb,
                collection = Db.users,
                id = creatorOid,
                projection = Projections.include("name"),
            ) ?: throw creatorNotFound()
            creatorMap[creatorOid] = UserName(creatorUserDoc.get("name"))
            filter["creator"] = creatorOid
        }
        var timeSort = -1
        when (val condition = input.condition) {
            is Condition.After -> {
                filter["creationTime"] = Document("\$gt", condition.time.utc)
                timeSort = 1
            }

            is Condition.Before -> filter["creationTime"] = Document("\$lt", condition.time.utc)
            null -> {}
        }
        val posts = Db.find<ObjectId>(
            db = deps.mongoDb,
            collection = Db.posts,
            configCollection = {
                this.find(filter)
                    .sort(Document("creationTime", timeSort))
                    .limit(input.size.value)
            }
        ).toList()
            .apply { if (this.isEmpty()) return emptyList() }
            .let { if (timeSort == 1) it.asReversed() else it }

        if (creatorMap.isEmpty()) {
            val creatorIds = posts.mapTo(HashSet()) { it.get<ObjectId>("creator") }
            val creatorDocs = Db.find<ObjectId>(
                db = deps.mongoDb,
                collection = Db.users,
                configCollection = {
                    this.find(Document("_id", Document("\$in", creatorIds)))
                        .projection(Projections.include("name", "_id"))
                }
            ).toList()
            for (creatorDoc in creatorDocs) {
                val creatorId = creatorDoc.get<ObjectId>("_id")
                creatorMap[creatorId] = UserName(creatorDoc.get("name"))
            }
        }

        return posts.map { postDoc ->
            val creatorId = postDoc.get<ObjectId>("creator")
            val creatorName = creatorMap[creatorId]
                ?: throw Exception("Invalid query result from `${Db.posts}[_id = ${postDoc.id}].creator`, not found in `${Db.users}`")

            Post(
                id = PostId(Db.formatId(postDoc.id)),
                title = Title(postDoc.get("title")),
                creator = Creator(
                    id = UserId(Db.formatId(creatorId)),
                    name = creatorName,
                ),
                creationTime = Time(postDoc.get("creationTime")),
            )
        }
    }
}
