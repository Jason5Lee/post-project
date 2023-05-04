package me.jason5lee.post_kt_vertx_fdm.common.api

import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody

object InvalidTextPostContent {
    internal val empty: FailureBody = FailureBody(
        error = Err(
            error = "TEXT_POST_CONTENT_EMPTY",
            reason = "Text post content cannot be empty",
            message = "Text post content cannot be empty",
        )
    )
    internal val tooLong: FailureBody = FailureBody(
        error = Err(
            error = "TEXT_POST_CONTENT_TOO_LONG",
            reason = "Text post content must be at most 65535 characters",
            message = "Text post content must be at most 65535 characters",
        )
    )
}
