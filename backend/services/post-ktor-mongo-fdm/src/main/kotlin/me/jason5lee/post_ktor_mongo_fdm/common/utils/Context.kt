package me.jason5lee.post_ktor_mongo_fdm.common.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import me.jason5lee.post_ktor_mongo_fdm.common.Identity
import me.jason5lee.post_ktor_mongo_fdm.common.Time
import me.jason5lee.post_ktor_mongo_fdm.common.api.badRequest
import me.jason5lee.post_ktor_mongo_fdm.common.api.getToken

class Context(
    val pipelineContext: PipelineContext<Unit, ApplicationCall>,
    val deps: Deps,
) {
    fun getCallerIdentity(): Identity? =
        getToken(pipelineContext.context.request.headers)?.let { token ->
            deps.auth.getIdentity(token)
        }

    fun getTokenExpireTime(): Time =
        deps.auth.getTokenExpireTime()

    fun generateToken(identity: Identity, expire: Time): String =
        deps.auth.generateToken(identity, expire)

    fun pathParameters(): Parameters =
        pipelineContext.context.parameters

    fun responseHeaders(): ResponseHeaders =
        pipelineContext.context.response.headers

    suspend inline fun <reified T : Any> getRequestBody(): T = try {
        pipelineContext.call.receive()
    } catch (e: BadRequestException) {
        throw badRequest("Invalid request body")
    }

    suspend inline fun <reified T : Any> respond(status: HttpStatusCode, body: T) {
        pipelineContext.call.respond(status, body)
    }

    fun respondNoContent() {
        pipelineContext.call.response.status(HttpStatusCode.NoContent)
    }
}

