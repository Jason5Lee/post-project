package me.jason5lee.post_ktor_mongo.common.utils

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.DotenvBuilder

class Env(
    val listenHost: String,
    val listenPort: Int,
    val mongoUrl: String,
    val mongoDatabase: String,
    val tokenValidSecs: Int,
    val tokenSecret: String,
    val adminToken: String,
    val encryptionCost: Int,
) {
    companion object {
        fun load(): Env {
            val dotenv = DotenvBuilder()
                .ignoreIfMissing()
                .load()

            return Env(
                listenHost = dotenv.getString("LISTEN_HOST"),
                listenPort = dotenv.getInt("LISTEN_PORT"),
                mongoUrl = dotenv.getString("MONGO_URL"),
                mongoDatabase = dotenv.getString("MONGO_DATABASE"),
                tokenValidSecs = dotenv.getInt("TOKEN_VALID_SECS"),
                tokenSecret = dotenv.getString("TOKEN_SECRET"),
                adminToken = dotenv.getString("ADMIN_TOKEN"),
                encryptionCost = dotenv.getIntOptional("ENCRYPTION_COST") ?: 10,
            )
        }
    }
}

private fun Dotenv.getString(name: String): String {
    return this[name] ?: throw IllegalStateException("Missing env var: '$name'")
}

private fun Dotenv.getInt(name: String): Int {
    val strValue = getString(name)
    return try {
        strValue.toInt()
    } catch (e: NumberFormatException) {
        throw IllegalStateException("Invalid env var: '$name'", e)
    }
}

private fun Dotenv.getIntOptional(name: String): Int? =
    this[name]?.let { strValue ->
        try {
            strValue.toInt()
        } catch (e: NumberFormatException) {
            throw IllegalStateException("Invalid env var: '$name'", e)
        }
    }
