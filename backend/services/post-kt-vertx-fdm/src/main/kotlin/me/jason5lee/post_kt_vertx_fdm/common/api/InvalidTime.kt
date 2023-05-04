package me.jason5lee.post_kt_vertx_fdm.common.api

import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody

object InvalidTime {
    val invalid: FailureBody = FailureBody(
        error = Err(
            error = "INVALID_TIME",
            reason = "Time must be a non-negative integer",
            message = "Time must be a non-negative integer",
        )
    )
}
