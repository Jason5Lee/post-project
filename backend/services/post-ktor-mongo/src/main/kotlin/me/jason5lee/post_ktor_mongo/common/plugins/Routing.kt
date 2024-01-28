// DO NOT EDIT

package me.jason5lee.post_ktor_mongo.common.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import me.jason5lee.post_ktor_mongo.common.api.ApiNotFound
import me.jason5lee.post_ktor_mongo.common.utils.Deps
import me.jason5lee.post_ktor_mongo.common.utils.add

internal fun Application.configureRouting(deps: Deps) {
    routing {
        add(me.jason5lee.post_ktor_mongo.create_post.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo.create_post.api)
        add(me.jason5lee.post_ktor_mongo.delete_post.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo.delete_post.api)
        add(me.jason5lee.post_ktor_mongo.edit_post.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo.edit_post.api)
        add(me.jason5lee.post_ktor_mongo.get_identity.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo.get_identity.api)
        add(me.jason5lee.post_ktor_mongo.get_post.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo.get_post.api)
        add(me.jason5lee.post_ktor_mongo.get_user.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo.get_user.api)
        add(me.jason5lee.post_ktor_mongo.list_posts.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo.list_posts.api)
        add(me.jason5lee.post_ktor_mongo.user_login.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo.user_login.api)
        add(me.jason5lee.post_ktor_mongo.user_register.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo.user_register.api)

        route("*") {
            handle {
                call.respond(ApiNotFound.status, ApiNotFound.body)
            }
        }
    }
}
