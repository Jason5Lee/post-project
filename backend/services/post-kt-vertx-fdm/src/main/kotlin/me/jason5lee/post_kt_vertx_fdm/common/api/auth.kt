package me.jason5lee.post_kt_vertx_fdm.common.api

import io.vertx.core.MultiMap
import me.jason5lee.post_kt_vertx_fdm.common.utils.Err
import me.jason5lee.post_kt_vertx_fdm.common.utils.FailureBody
import me.jason5lee.post_kt_vertx_fdm.common.utils.HttpException

private const val BEARER = "Bearer "
fun getToken(headers: MultiMap): String? =
    headers["Authorization"]?.let { authHeader ->
        if (!authHeader.startsWith(BEARER)) {
            throw invalidAuth()
        }
        return authHeader.substring(BEARER.length)
    }

fun invalidAuth(): HttpException = HttpException(
    HttpStatusCode.Unauthorized,
    FailureBody(
        Err(
            error = "INVALID_AUTH",
            reason = "The authorization is invalid",
            // When this happens, the client should remove the token instead of showing this message.
            message = me.jason5lee.post_kt_vertx_fdm.common.api.clientBugMessage,
        )
    )
)
