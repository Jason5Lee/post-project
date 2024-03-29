package me.jason5lee.post_ktor_mongo.common.utils

import io.ktor.http.*
import kotlinx.serialization.Serializable

class HttpException(
    val status: HttpStatusCode,
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
)
