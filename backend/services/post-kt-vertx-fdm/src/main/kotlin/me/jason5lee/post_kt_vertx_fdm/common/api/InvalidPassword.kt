package me.jason5lee.post_kt_vertx_fdm.common.api

import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody

internal object InvalidPassword {
    internal val empty: FailureBody = FailureBody(
        error = Err(
            error = "PASSWORD_EMPTY",
            reason = "Password cannot be empty",
            message = "Password cannot be empty",
        )
    )
    internal val tooShort: FailureBody = FailureBody(
        error = Err(
            error = "PASSWORD_TOO_SHORT",
            reason = "Password must be at least 5 characters",
            message = "Password must be at least 5 characters",
        )
    )
    internal val tooLong: FailureBody = FailureBody(
        error = Err(
            error = "PASSWORD_TOO_LONG",
            reason = "Due to the limitation of the bcrypt algorithm, password must be at most 72 characters",
            message = "Password must be at most 72 characters",
        )
    )
}
