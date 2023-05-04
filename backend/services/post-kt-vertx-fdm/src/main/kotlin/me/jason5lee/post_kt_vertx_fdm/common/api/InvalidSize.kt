package me.jason5lee.post_kt_vertx_fdm.common.api

import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody

object InvalidSize {
    val nonPositiveInteger: FailureBody = FailureBody(
        error = Err(
            error = "INVALID_SIZE",
            reason = "Size must be a positive integer",
            message = "Size must be a positive integer",
        )
    )
}
