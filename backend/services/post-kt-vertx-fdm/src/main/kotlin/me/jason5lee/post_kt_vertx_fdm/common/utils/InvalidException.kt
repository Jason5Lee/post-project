package me.jason5lee.post_kt_vertx_fdm.common.utils

interface InvalidException {
    operator fun invoke(body: FailureBody): Exception
}

class OnInvalidRespond(private val statusCode: Int, private val prefix: String? = null) : InvalidException {
    override fun invoke(body: FailureBody): Exception =
        if (prefix == null) {
            HttpException(statusCode, body)
        } else {
            HttpException(
                statusCode, FailureBody(
                    error = body.error.copy(error = prefix + body.error.error)
                )
            )
        }
}

inline fun onInvalid(crossinline block: (FailureBody) -> Exception): InvalidException =
    object : InvalidException {
        override fun invoke(body: FailureBody): Exception = block(body)
    }
