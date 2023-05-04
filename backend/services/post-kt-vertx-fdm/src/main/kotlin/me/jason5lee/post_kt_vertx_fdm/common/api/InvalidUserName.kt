package me.jason5lee.post_kt_vertx_fdm.common.api

import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody

internal object InvalidUserName {
    internal val empty: FailureBody = FailureBody(
        error = Err(
            error = "USER_NAME_EMPTY",
            reason = "User name cannot be empty",
            message = "User name cannot be empty",
        )
    )
    internal val tooShort: FailureBody = FailureBody(
        error = Err(
            error = "USER_NAME_TOO_SHORT",
            reason = "User name must be at least 3 characters",
            message = "User name must be at least 3 characters",
        )
    )
    internal val tooLong: FailureBody = FailureBody(
        error = Err(
            error = "USER_NAME_TOO_LONG",
            reason = "User name must be at most 20 characters",
            message = "User name must be at most 20 characters",
        )
    )
    internal val containsIllegalCharacter: FailureBody = FailureBody(
        error = Err(
            error = "USER_NAME_ILLEGAL",
            reason = "User name can only contain letters, numbers, and underscores",
            message = "User name can only contain letters, numbers, and underscores",
        )
    )
}
