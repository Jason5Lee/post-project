package me.jason5lee.post_kt_vertx_fdm.common.utils

sealed class ValidationResult<out T> {
    abstract fun onInvalidRespond(statusCode: Int): T
    abstract fun onInvalidRespond(statusCode: Int, errorPrefix: String): T

    data class Valid<T>(val value: T) : ValidationResult<T>() {
        override fun onInvalidRespond(statusCode: Int): T = value
        override fun onInvalidRespond(statusCode: Int, errorPrefix: String): T = value
    }

    data class Invalid(val body: FailureBody) : ValidationResult<Nothing>() {
        override fun onInvalidRespond(statusCode: Int): Nothing {
            throw HttpException(statusCode, body)
        }

        override fun onInvalidRespond(statusCode: Int, errorPrefix: String): Nothing {
            throw HttpException(
                statusCode, FailureBody(
                    error = Err(
                        error = errorPrefix + body.error.error,
                        reason = body.error.reason,
                        message = body.error.message,
                    )
                )
            )
        }
    }
}

inline fun <T> ValidationResult<T>.onInvalidThrow(exception: (ValidationResult.Invalid) -> Throwable): T {
    return when (this) {
        is ValidationResult.Valid -> value
        is ValidationResult.Invalid -> throw exception(this)
    }
}
