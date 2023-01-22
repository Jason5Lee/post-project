package me.jason5lee.post_ktor_mongo_fdm.common.api

import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody

val somethingWentWrongMessage = "Something went wrong, please try again later."

fun internalServerError(id: String): FailureBody = FailureBody(
    error = Err(
        error = "INTERNAL_SERVER_ERROR",
        reason = "An internal server error occurred, trace ID: $id",
        message = somethingWentWrongMessage,
    )
)
