package me.jason5lee.post_ktor_mongo_fdm.common.utils

import io.ktor.http.*
import io.ktor.server.routing.*
import me.jason5lee.post_ktor_mongo_fdm.common.api.internalServerError
import java.util.*

interface Api<W> {
    fun addToRouter(routing: Routing, deps: Deps, workflow: W)

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

        // cannot be inline because of https://youtrack.jetbrains.com/issue/KT-55939/
        fun <W> create(method: HttpMethod, path: String, handler: suspend (Context, W) -> Unit): Api<W> = object : Api<W> {
            override fun addToRouter(routing: Routing, deps: Deps, workflow: W) {
                routing.route(path, method) {
                    handle {
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
    }
}

fun <W> Routing.add(workflow: W, deps: Deps, api: Api<W>) {
    api.addToRouter(this, deps, workflow)
}
