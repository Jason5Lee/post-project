package me.jason5lee.post_ktor_mongo_fdm.common.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import io.ktor.server.application.*
import io.ktor.util.logging.*

class Deps(
    val auth: Auth,
    val mongoDb: MongoDatabase,
    val bCrypt: Db.BCrypt,
    val logger: Logger,
) {
    companion object {
        fun fromEnv(application: Application, env: Env): Deps =
            Deps(
                run {
                    val algorithm = Algorithm.HMAC256(env.tokenSecret)
                    Auth(
                        JWT.require(algorithm).build(),
                        algorithm,
                        env.tokenValidSecs,
                        env.adminToken,
                    )
                },
                MongoClients.create(env.mongoUrl).getDatabase(env.mongoDatabase),
                Db.BCrypt(env.encryptionCost),
                application.log,
            )
    }
}
