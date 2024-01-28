package me.jason5lee.post_ktor_mongo.common.utils

import com.mongodb.client.model.Filters
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.reactivestreams.client.FindPublisher
import com.mongodb.reactivestreams.client.MongoDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.reactivestreams.Publisher
import java.util.*

object Db {
    val users = "users"
    val posts = "posts"

    suspend fun initDB(db: MongoDatabase) {
        db.getCollection(users).createIndex(Indexes.ascending("name"), IndexOptions().unique(true)).awaitFirstOrNull()
        db.getCollection(posts).createIndex(Indexes.ascending("title"), IndexOptions().unique(true)).awaitFirstOrNull()
        db.getCollection(posts).createIndex(Indexes.ascending("creator")).awaitFirstOrNull()
        db.getCollection(posts).createIndex(Indexes.descending("creationTime", "_id")).awaitFirstOrNull()
    }

    class QueryDoc<Id : Any>(val collection: String, val id: Id, val doc: Document) {
        inline fun <reified T : Any> get(field: String): T {
            val value = doc[field] ?: throw fieldNotFoundException(collection, id, field)
            if (value is T) {
                return value
            } else {
                throw fieldTypeMismatchException(collection, id, field, value, T::class.java)
            }
        }

        inline fun <reified T : Any> getOptional(field: String): T? {
            val value = doc[field] ?: return null
            if (value is T) {
                return value
            } else {
                throw fieldTypeMismatchException(collection, id, field, value, T::class.java)
            }
        }
    }

    suspend fun <Id : Any> findById(db: MongoDatabase, collection: String, id: Id, projection: Bson?): QueryDoc<Id>? =
        db.getCollection(collection).find(Filters.eq("_id", id)).projection(projection).first().awaitFirstOrNull()
            ?.let { QueryDoc(collection, id, it) }

    inline fun <reified Id : Any> find(
        db: MongoDatabase,
        collection: String,
        configCollection: com.mongodb.reactivestreams.client.MongoCollection<Document>.() -> Publisher<Document>
    ): Flow<QueryDoc<Id>> =
        db.getCollection(collection).configCollection()
            .asFlow()
            .map {
                val idAny = it["_id"] ?: throw noIdException(collection)
                if (idAny is Id) {
                    QueryDoc(collection, idAny, it)
                } else {
                    throw invalidIdException(collection, idAny, Id::class.java)
                }
            }

    suspend inline fun <reified Id : Any> findOne(
        db: MongoDatabase,
        collection: String,
        configCollection: com.mongodb.reactivestreams.client.MongoCollection<Document>.() -> FindPublisher<Document>
    ): QueryDoc<Id>? =
        db.getCollection(collection).configCollection()
            .first()
            .awaitFirstOrNull()
            ?.let {
                val idAny = it["_id"] ?: throw noIdException(collection)
                if (idAny is Id) {
                    QueryDoc(collection, idAny, it)
                } else {
                    throw invalidIdException(collection, idAny, Id::class.java)
                }
            }

    suspend fun countExact(
        db: MongoDatabase,
        collection: String,
        filter: Document,
    ): Long =
        db.getCollection(collection)
            .countDocuments(filter)
            .awaitSingle()

    private val idEncoder = Base64.getUrlEncoder().withoutPadding()
    private val idDecoder = Base64.getUrlDecoder()

    fun toObjectIdOrNull(id: String): ObjectId? {
        val idBytes = try {
            idDecoder.decode(id)
        } catch (e: IllegalArgumentException) {
            return null
        }
        return try {
            ObjectId(idBytes)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun formatId(oid: ObjectId): String {
        return idEncoder.encodeToString(oid.toByteArray())
    }

    class BCrypt(private val cost: Int) : PasswordEncryptor {
        private val bCrypt = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults()
        override fun encrypt(plain: String): String = bCrypt.hashToString(cost, plain.toCharArray())

        class Verifier(private val hash: String) : PasswordVerifier {
            override fun verify(plain: String): Boolean =
                at.favre.lib.crypto.bcrypt.BCrypt.verifyer().verify(plain.toCharArray(), hash).verified
        }
    }

    @PublishedApi
    internal fun fieldNotFoundException(collection: String, id: Any, field: String): Exception =
        Exception("Invalid query result from `$collection[_id = $id]`, field `$field` not found")

    fun fieldTypeMismatchException(
        collection: String,
        id: Any,
        field: String,
        value: Any,
        expectedClass: Class<*>
    ): Exception =
        Exception("Invalid query result from `$collection[_id = $id].$field`, value `$value` is of type `${value.javaClass.simpleName}`, expected `${expectedClass.simpleName}`")

    fun invalidException(collection: String, id: Any, field: String, value: Any, invalidBody: FailureBody): Exception =
        Exception("Invalid query result from `$collection[_id = $id].$field`, value `$value` is invalid, ${invalidBody.error.error}: ${invalidBody.error.reason}")

    fun noIdException(collection: String): Exception =
        Exception("Invalid query result from `$collection`, document has no `_id` field")

    fun invalidIdException(collection: String, id: Any, clazz: Class<*>): Exception =
        Exception("Invalid query result from `$collection`, _id `$id` is of type `${id.javaClass.simpleName}`, expected `${clazz}`")
}
