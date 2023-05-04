package me.jason5lee.post_kt_vertx_fdm

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router

class MainVerticle : AbstractVerticle() {

    override fun start(startPromise: Promise<Void>) {
        val router = Router.router(vertx)
        router.route().handler {
            val headers = it.request().headers()
        }

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
