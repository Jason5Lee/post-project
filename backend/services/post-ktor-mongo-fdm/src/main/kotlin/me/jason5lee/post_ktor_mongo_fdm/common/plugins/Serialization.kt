package me.jason5lee.post_ktor_mongo_fdm.common.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kotlinx.serialization.json.Json

internal fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            encodeDefaults = false
        })
    }
}
