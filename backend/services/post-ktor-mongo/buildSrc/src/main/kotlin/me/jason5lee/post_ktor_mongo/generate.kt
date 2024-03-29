package me.jason5lee.post_ktor_mongo

import java.io.File

private val customWorkflowImpl: Map<String, String> = mapOf()

fun generateRouting() {
    File("src/main/kotlin/me/jason5lee/post_ktor_mongo/common/plugins/Routing.kt")
        .bufferedWriter(Charsets.UTF_8)
        .use { writer ->
            writer.write(
                "// DO NOT EDIT\n" +
                        "\n" +
                        "package me.jason5lee.post_ktor_mongo.common.plugins\n" +
                        "\n" +
                        "import io.ktor.server.routing.*\n" +
                        "import io.ktor.server.application.*\n" +
                        "import io.ktor.server.response.*\n" +
                        "import me.jason5lee.post_ktor_mongo.common.api.ApiNotFound\n" +
                        "import me.jason5lee.post_ktor_mongo.common.utils.Deps\n" +
                        "import me.jason5lee.post_ktor_mongo.common.utils.add\n" +
                        "\n" +
                        "internal fun Application.configureRouting(deps: Deps) {\n" +
                        "    routing {\n"
            )
            val files = File("src/main/kotlin/me/jason5lee/post_ktor_mongo").listFiles()
            files?.forEach { file ->
                val name = file.name
                if (file.isDirectory && name != "common") {
                    val workflowImpl = customWorkflowImpl[file.name] ?: "WorkflowImpl(deps)"
                    writer.write("        add(me.jason5lee.post_ktor_mongo.${file.name}.${workflowImpl}, deps, me.jason5lee.post_ktor_mongo.${file.name}.api)\n")
                }
            }
            writer.write(
                "\n        route(\"*\") {\n" +
                        "            handle {\n" +
                        "                call.respond(ApiNotFound.status, ApiNotFound.body)\n" +
                        "            }\n" +
                        "        }\n    }\n}\n"
            )
        }
}
