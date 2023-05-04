package me.jason5lee.post_kt_vertx_fdm.common.api

import io.ktor.http.*
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody
import me.jason5lee.post_ktor_mongo_fdm.common.utils.HttpException

val clientBugMessage = "Something went wrong. Looks like a bug of the client. Please report this issue."
fun badRequest(reason: String): HttpException = HttpException(
    HttpStatusCode.BadRequest,
    FailureBody(
        Err(
            error = "BAD_REQUEST",
            reason = reason,
            message = me.jason5lee.post_kt_vertx_fdm.common.api.clientBugMessage,
        )
    )
)
