package me.jason5lee.post_kt_vertx_fdm.common.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

internal fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            encodeDefaults = false
        })
    }
}
