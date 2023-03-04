// DO NOT EDIT

package me.jason5lee.post_ktor_mongo_fdm.common.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import me.jason5lee.post_ktor_mongo_fdm.common.api.ApiNotFound
import me.jason5lee.post_ktor_mongo_fdm.common.utils.Deps
import me.jason5lee.post_ktor_mongo_fdm.common.utils.add

internal fun Application.configureRouting(deps: Deps) {
    routing {
        add(me.jason5lee.post_ktor_mongo_fdm.edit_post.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo_fdm.edit_post.api)
        add(me.jason5lee.post_ktor_mongo_fdm.list_posts.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo_fdm.list_posts.api)
        add(me.jason5lee.post_ktor_mongo_fdm.create_post.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo_fdm.create_post.api)
        add(me.jason5lee.post_ktor_mongo_fdm.get_identity.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo_fdm.get_identity.api)
        add(me.jason5lee.post_ktor_mongo_fdm.admin_login.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo_fdm.admin_login.api)
        add(me.jason5lee.post_ktor_mongo_fdm.get_user.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo_fdm.get_user.api)
        add(me.jason5lee.post_ktor_mongo_fdm.delete_post.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo_fdm.delete_post.api)
        add(me.jason5lee.post_ktor_mongo_fdm.user_login.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo_fdm.user_login.api)
        add(me.jason5lee.post_ktor_mongo_fdm.user_register.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo_fdm.user_register.api)
        add(me.jason5lee.post_ktor_mongo_fdm.get_post.WorkflowImpl(deps), deps, me.jason5lee.post_ktor_mongo_fdm.get_post.api)

        route("*") {
            handle {
                call.respond(ApiNotFound.status, ApiNotFound.body)
            }
        }
    }
}
