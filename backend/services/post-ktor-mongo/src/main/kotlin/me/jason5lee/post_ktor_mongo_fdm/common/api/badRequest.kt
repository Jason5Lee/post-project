package me.jason5lee.post_ktor_mongo.common.api

import io.ktor.http.*
import me.jason5lee.post_ktor_mongo.common.utils.Err
import me.jason5lee.post_ktor_mongo.common.utils.FailureBody
import me.jason5lee.post_ktor_mongo.common.utils.HttpException

fun badRequest(reason: String): HttpException = HttpException(
    HttpStatusCode.BadRequest,
    FailureBody(
        Err(
            error = "BAD_REQUEST",
            reason = reason,
        )
    )
)
