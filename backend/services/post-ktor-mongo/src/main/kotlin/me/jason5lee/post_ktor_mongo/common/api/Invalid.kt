package me.jason5lee.post_ktor_mongo.common.api

import me.jason5lee.post_ktor_mongo.common.utils.Err
import me.jason5lee.post_ktor_mongo.common.utils.FailureBody

object Invalid {
    val userName = FailureBody(
        error = Err(
            error = "INVALID_USER_NAME",
            reason = "The user name is invalid"
        )
    )

    val title = FailureBody(
        error = Err(
            error = "INVALID_TITLE",
            reason = "The title is invalid"
        )
    )

    val textPostContent = FailureBody(
        error = Err(
            error = "INVALID_TEXT_POST_CONTENT",
            reason = "The content of the text post is invalid"
        )
    )

    val urlPostContent = FailureBody(
        error = Err(
            error = "INVALID_URL_POST_CONTENT",
            reason = "The content of the URL post is invalid"
        )
    )

    val password = FailureBody(
        error = Err(
            error = "INVALID_PASSWORD",
            reason = "The password is invalid"
        )
    )

    val time = FailureBody(
        error = Err(
            error = "INVALID_TIMESTAMP",
            reason = "The timestamp is invalid"
        )
    )

    val page = FailureBody(
        error = Err(
            error = "INVALID_PAGE",
            reason = "The page number is invalid"
        )
    )

    val pageSize = FailureBody(
        error = Err(
            error = "INVALID_PAGE_SIZE",
            reason = "The page size is invalid"
        )
    )
}
