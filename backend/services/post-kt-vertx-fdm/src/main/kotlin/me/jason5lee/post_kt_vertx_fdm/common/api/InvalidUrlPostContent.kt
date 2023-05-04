package me.jason5lee.post_kt_vertx_fdm.common.api

import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody

internal object InvalidUrlPostContent {
    internal val empty: FailureBody = FailureBody(
        error = Err(
            error = "URL_POST_CONTENT_EMPTY",
            reason = "URL post content cannot be empty",
            message = "URL post content cannot be empty",
        )
    )
    internal val tooLong: FailureBody = FailureBody(
        error = Err(
            error = "URL_POST_CONTENT_TOO_LONG",
            reason = "URL post content must be at most 65535 characters",
            message = "URL post content must be at most 65535 characters",
        )
    )

    fun invalid(reason: String?): FailureBody = FailureBody(
        error = Err(
            error = "URL_POST_CONTENT_INVALID",
            reason = reason ?: "URL post content must be a valid URL",
            message = "URL post content must be a valid URL",
        )
    )
}
