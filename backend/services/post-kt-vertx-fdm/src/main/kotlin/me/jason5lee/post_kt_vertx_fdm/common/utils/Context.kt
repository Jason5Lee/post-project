package me.jason5lee.post_kt_vertx_fdm.common.utils

import io.vertx.core.MultiMap
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import me.jason5lee.post_kt_vertx_fdm.common.api.getToken

class Context(
    val routingContext: RoutingContext,
    val deps: Deps,
) {
    fun getCallerIdentity(): me.jason5lee.post_kt_vertx_fdm.common.Identity? =
        getToken(routingContext.request().headers())?.let { token ->
            deps.auth.getIdentity(token)
        }

    fun getTokenExpireTime(): me.jason5lee.post_kt_vertx_fdm.common.Time =
        deps.auth.getTokenExpireTime()

    fun generateToken(identity: me.jason5lee.post_kt_vertx_fdm.common.Identity, expire: me.jason5lee.post_kt_vertx_fdm.common.Time): String =
        deps.auth.generateToken(identity, expire)

    fun pathParameters(): Map<String, String> =
        routingContext.pathParams()

    fun responseHeaders(): MultiMap =
        routingContext.response().headers()

    inline fun <reified T : Any> getRequestBody(): T = try {
        json.decodeFromString(routingContext.body().asString())
    } catch (e: kotlinx.serialization.SerializationException) {
        throw me.jason5lee.post_kt_vertx_fdm.common.api.badRequest("Invalid request body")
    }

    inline fun <reified T : Any> respond(statusCode: Int, body: T) {
        val resp = routingContext.response()
        resp.statusCode = statusCode
        resp.putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        resp.end(json.encodeToString(body))
    }

    fun respondNoContent() {
        routingContext.response().statusCode = 204
    }

    companion object {
        val json = kotlinx.serialization.json.Json {
            encodeDefaults = false
        }
    }
}

