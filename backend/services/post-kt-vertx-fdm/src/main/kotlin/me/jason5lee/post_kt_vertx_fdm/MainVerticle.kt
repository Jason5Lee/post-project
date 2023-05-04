package me.jason5lee.post_kt_vertx_fdm

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainVerticle : CoroutineVerticle() {
    override suspend fun start() {
        coroutineScope {

        }
    }

    override fun start(startPromise: Promise<Void>) {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())

        router.route().handler {
            val headers = it.request().headers()
        }
        router.route(HttpMethod.POST, "/test")
            .handler()

        vertx
            .createHttpServer()
            .requestHandler { req ->
                req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x!")
            }
            .listen(8888) { http ->
                if (http.succeeded()) {
                    startPromise.complete()
                    println("HTTP server started on port 8888")
                } else {
                    startPromise.fail(http.cause());
                }
            }
    }
}
