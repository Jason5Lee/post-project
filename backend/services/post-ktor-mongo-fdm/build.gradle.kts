import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.8.0"
    id("io.ktor.plugin") version "2.2.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
}

group = "me.jason5lee"
version = "0.0.1"
application {
    mainClass.set("me.jason5lee.post_ktor_mongo_fdm.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("com.auth0:java-jwt:4.2.1")

    implementation("org.mongodb:mongodb-driver-reactivestreams:4.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.6.4")

    implementation("io.github.cdimascio:dotenv-kotlin:6.4.0")

    implementation("at.favre.lib:bcrypt:0.9.0")

    implementation("me.jason5lee:resukt:1.0.0")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks.register<Task>("generateRouting") {
    group = "source-generation"
    description = "Generate function that routes the endpoint of all workflows"

    doLast {
        me.jason5lee.post_ktor_mongo_fdm.generateRouting()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
