package me.jason5lee.post_ktor_mongo_fdm

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import me.jason5lee.post_ktor_mongo_fdm.common.plugins.configureRouting
import me.jason5lee.post_ktor_mongo_fdm.common.plugins.configureSerialization
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Db
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Env

fun main() {
    val env = Env.load()

    embeddedServer(Netty, port = env.listenPort, host = env.listenHost, module = { module(env) })
        .start(wait = true)
}

fun Application.module(env: Env) {
    val deps = Deps.fromEnv(this, env)
    runBlocking {
        Db.initDB(deps.mongoDb)
    }
    configureSerialization()
    configureRouting(deps)
}
