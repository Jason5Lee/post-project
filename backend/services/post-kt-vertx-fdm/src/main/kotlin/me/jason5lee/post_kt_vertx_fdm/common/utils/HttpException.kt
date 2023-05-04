package me.jason5lee.post_kt_vertx_fdm.common.utils

import io.ktor.http.*
import kotlinx.serialization.Serializable

class HttpException(
    val status: Int,
    val body: FailureBody,
) : Exception()

@Serializable
data class FailureBody(
    val error: Err,
)

@Serializable
data class Err(
    val error: String,
    val reason: String,
    val message: String,
)
