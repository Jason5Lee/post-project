package me.jason5lee.post_ktor_mongo_fdm.common.utils

import io.ktor.http.*

sealed class ValidationResult<out T> {
    abstract fun onInvalidRespond(statusCode: HttpStatusCode): T
    abstract fun onInvalidRespond(statusCode: HttpStatusCode, errorPrefix: String): T
    abstract fun assertValid(): T

    data class Valid<T>(val value: T) : ValidationResult<T>() {
        override fun onInvalidRespond(statusCode: HttpStatusCode): T = value
        override fun onInvalidRespond(statusCode: HttpStatusCode, errorPrefix: String): T = value
        override fun assertValid(): T = value
    }
    data class Invalid(val body: FailureBody, val value: Any) : ValidationResult<Nothing>() {
        override fun onInvalidRespond(statusCode: HttpStatusCode): Nothing {
            throw HttpException(statusCode, body)
        }

        override fun onInvalidRespond(statusCode: HttpStatusCode, errorPrefix: String): Nothing {
            throw HttpException(statusCode, FailureBody(
                error = Err(
                    error = errorPrefix + body.error.error,
                    reason = body.error.reason,
                    message =  body.error.message,
                )
            ))
        }

        override fun assertValid(): Nothing {
            throw AssertionError("Invalid value, value: `$value`, ${body.error.error}: ${body.error.reason}")
        }
    }
}

inline fun <T> ValidationResult<T>.onInvalidThrow(exception: (ValidationResult.Invalid) -> Throwable): T {
    return when (this) {
        is ValidationResult.Valid -> value
        is ValidationResult.Invalid -> throw exception(this)
    }
}
