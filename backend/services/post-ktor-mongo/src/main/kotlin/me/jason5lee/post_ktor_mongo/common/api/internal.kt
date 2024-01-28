package me.jason5lee.post_ktor_mongo.common.api

import me.jason5lee.post_ktor_mongo.common.utils.Err
import me.jason5lee.post_ktor_mongo.common.utils.FailureBody

fun internalServerError(id: String): FailureBody = FailureBody(
    error = Err(
        error = "INTERNAL_SERVER_ERROR",
        reason = "An internal server error occurred, trace ID: $id",
    )
)
