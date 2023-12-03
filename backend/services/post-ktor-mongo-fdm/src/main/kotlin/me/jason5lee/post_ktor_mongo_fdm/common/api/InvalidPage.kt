package me.jason5lee.post_ktor_mongo_fdm.common.api

import me.jason5lee.post_ktor_mongo_fdm.common.utils.Err
import me.jason5lee.post_ktor_mongo_fdm.common.utils.FailureBody

object InvalidPage {
    val invalidPage: FailureBody = FailureBody(
        error = Err(
            error = "INVALID_PAGE",
            reason = "page is invalid",
            message = "page is invalid",
        )
    )
}
