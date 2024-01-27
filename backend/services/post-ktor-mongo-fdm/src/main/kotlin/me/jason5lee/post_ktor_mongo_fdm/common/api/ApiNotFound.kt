package me.jason5lee.post_ktor_mongo_fdm.common.api

import io.ktor.http.*
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody

object ApiNotFound {
    val status = HttpStatusCode.NotFound
    val body = FailureBody(
        error = Err(
            error = "API_NOT_FOUND",
            reason = "API not found. Please check API path and method.",
        )
    )
}
