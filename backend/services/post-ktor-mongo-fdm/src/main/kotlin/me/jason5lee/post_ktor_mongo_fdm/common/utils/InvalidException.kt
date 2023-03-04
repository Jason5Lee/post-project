package me.jason5lee.post_ktor_mongo_fdm.common.utils

import io.ktor.http.*

interface InvalidException {
    operator fun invoke(body: FailureBody): Exception
}

class OnInvalidRespond(private val status: HttpStatusCode, private val prefix: String? = null) : InvalidException {
    override fun invoke(body: FailureBody): Exception =
        if (prefix == null) {
            HttpException(status, body)
        } else {
            HttpException(
                status, FailureBody(
                    error = body.error.copy(error = prefix + body.error.error)
                )
            )
        }
}

inline fun onInvalid(crossinline block: (FailureBody) -> Exception): InvalidException =
    object : InvalidException {
        override fun invoke(body: FailureBody): Exception = block(body)
    }
