package me.jason5lee.post_ktor_mongo_fdm.common.api

import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody

object InvalidPageSize {
    val invalidPageSize: FailureBody = FailureBody(
        error = Err(
            error = "INVALID_PAGE_SIZE",
            reason = "page size is invalid",
            message = "page size is invalid",
        )
    )

    val pageSizeTooLarge: FailureBody = FailureBody(
        error = Err(
            error = "PAGE_SIZE_TOO_LARGE",
            reason = "page size is too large",
            message = "page size is too large",
        )
    )
}
