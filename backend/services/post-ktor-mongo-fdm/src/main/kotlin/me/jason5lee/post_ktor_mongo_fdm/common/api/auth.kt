package me.jason5lee.post_ktor_mongo_fdm.common.api

import io.ktor.http.*
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody
import me.jason5lee.post_ktor_mongo_fdm.common.utils.HttpException

sealed class Token {
    data class User(val value: String) : Token()
    data class Admin(val value: String) : Token()
}

private const val USER_PREFIX = "Bearer "
private const val ADMIN_PREFIX = "Admin "

fun getToken(headers: Headers): Token? =
    headers["Authorization"]?.let { authHeader ->
        if (authHeader.startsWith(USER_PREFIX)) {
            return Token.User(authHeader.substring(USER_PREFIX.length))
        }
        if (authHeader.startsWith(ADMIN_PREFIX)) {
            return Token.Admin(authHeader.substring(ADMIN_PREFIX.length))
        }
        throw invalidAuth()
    }

fun invalidAuth(): HttpException = HttpException(
    HttpStatusCode.Unauthorized,
    FailureBody(
        Err(
            error = "INVALID_AUTH",
            reason = "The authorization is invalid",
            // When this happens, the client should remove the token instead of showing this message.
            message = clientBugMessage,
        )
    )
)
