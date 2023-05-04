package me.jason5lee.post_kt_vertx_fdm.common.api

import me.jason5lee.post_kt_vertx_fdm.common.utils.Err
import me.jason5lee.post_kt_vertx_fdm.common.utils.FailureBody

object ApiNotFound {
    val status = 404
    val body = FailureBody(
        error = Err(
            error = "API_NOT_FOUND",
            reason = "The API does not exist",
            message = me.jason5lee.post_kt_vertx_fdm.common.api.clientBugMessage,
        )
    )
}
