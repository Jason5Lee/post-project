package me.jason5lee.post_kt_vertx_fdm.common.utils

import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import me.jason5lee.post_kt_vertx_fdm.common.api.internalServerError
import java.util.*
import kotlinx.coroutines.launch

abstract class HttpApi<W>(val method: HttpMethod, val path: String) {
    abstract fun createHandler(scope: CoroutineScope, deps: Deps, workflow: W): Handler<RoutingContext>

    companion object {
        suspend fun handleException(ctx: Context, e: Throwable) {
            if (e is HttpException) {
                ctx.deps.logger.error("", e)
                ctx.respond(e.status, e.body)
            } else {
                val id = UUID.randomUUID().toString()
                ctx.deps.logger.error("[$id]", e)
                ctx.respond(500, internalServerError(id))
            }
        }

        inline operator fun <W> invoke(
            method: HttpMethod,
            path: String,
            crossinline handler: suspend (Context, W) -> Unit
        ): HttpApi<W> =
            object : HttpApi<W>(method, path) {
                override fun createHandler(
                    scope: CoroutineScope,
                    deps: Deps,
                    workflow: W
                ): Handler<RoutingContext> = Handler<RoutingContext> { routingContext ->
                    val context = Context(routingContext, deps)

                    scope.launch(routingContext.vertx().dispatcher()) {
                        try {
                            handler(context, workflow)
                        } catch (e: Throwable) {
                            handleException(context, e)
                        }
                    }
                }
            }
    }
}

fun <W> Router.add(scope: CoroutineScope, workflow: W, deps: Deps, api: HttpApi<W>) {
    route(api.method, api.path).handler(api.createHandler(scope, deps, workflow))
}
