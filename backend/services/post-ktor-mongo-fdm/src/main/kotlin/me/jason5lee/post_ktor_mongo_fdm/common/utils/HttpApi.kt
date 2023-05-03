package me.jason5lee.post_ktor_mongo_fdm.common.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import me.jason5lee.post_ktor_mongo_fdm.common.api.internalServerError
import java.util.*

abstract class HttpApi<W>(private val method: HttpMethod, private val path: String) {
    abstract fun createHandler(deps: Deps, workflow: W): suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit

    fun addToRouter(routing: Routing, deps: Deps, workflow: W) {
        routing.route(path, method) {
            handle(createHandler(deps, workflow))
        }
    }

    companion object {
        suspend fun handleException(ctx: Context, e: Throwable) {
            if (e is HttpException) {
                ctx.deps.logger.error("", e)
                ctx.respond(e.status, e.body)
            } else {
                val id = UUID.randomUUID().toString()
                ctx.deps.logger.error("[$id]", e)
                ctx.respond(HttpStatusCode.InternalServerError, internalServerError(id))
            }
        }

        inline operator fun <W> invoke(
            method: HttpMethod,
            path: String,
            crossinline handler: suspend (Context, W) -> Unit
        ): HttpApi<W> =
            object : HttpApi<W>(method, path) {
                override fun createHandler(
                    deps: Deps,
                    workflow: W
                ): suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit =
                    {
                        val context = Context(this, deps)
                        try {
                            handler(context, workflow)
                        } catch (e: Throwable) {
                            handleException(context, e)
                        }
                    }
            }
    }
}

fun <W> Routing.add(workflow: W, deps: Deps, api: HttpApi<W>) {
    api.addToRouter(this, deps, workflow)
}
