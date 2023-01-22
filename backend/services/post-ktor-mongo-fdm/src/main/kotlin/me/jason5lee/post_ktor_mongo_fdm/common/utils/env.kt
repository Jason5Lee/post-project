package me.jason5lee.post_ktor_mongo_fdm.common.utils

import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.application.*
class Env(
    val listenHost: String,
    val listenPort: Int,
    val mongoUrl: String,
    val mongoDatabase: String,
    val tokenValidSecs: Int,
    val tokenSecret: String,
    val encryptionCost: Int,
) {
    companion object {
        fun load(): Env {
            val dotenv = Dotenv.load()
            return Env(
                listenHost = dotenv.getString("LISTEN_HOST"),
                listenPort = dotenv.getInt("LISTEN_PORT"),
                mongoUrl = dotenv.getString("MONGO_URL"),
                mongoDatabase = dotenv.getString("MONGO_DATABASE"),
                tokenValidSecs = dotenv.getInt("TOKEN_VALID_SECS"),
                tokenSecret = dotenv.getString("TOKEN_SECRET"),
                encryptionCost = dotenv.getInt("ENCRYPTION_COST"),
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
