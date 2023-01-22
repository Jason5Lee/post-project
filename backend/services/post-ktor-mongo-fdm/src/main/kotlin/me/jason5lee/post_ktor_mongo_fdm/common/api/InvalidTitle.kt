package me.jason5lee.post_ktor_mongo_fdm.common.api

import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody

internal object InvalidTitle {
    internal val empty: FailureBody = FailureBody(
        error = Err(
            error = "TITLE_EMPTY",
            reason = "Title cannot be empty",
            message = "Title cannot be empty",
        )
    )
    internal val tooShort: FailureBody = FailureBody(
        error = Err(
            error = "TITLE_TOO_SHORT",
            reason = "Title must be at least 3 characters",
            message = "Title must be at least 3 characters",
        )
    )
    internal val tooLong: FailureBody = FailureBody(
        error = Err(
            error = "TITLE_TOO_LONG",
            reason = "Title must be at most 20 characters",
            message = "Title must be at most 20 characters",
        )
    )
}