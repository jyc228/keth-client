plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.0.21"
    `maven-publish`
}

dependencies {
    api(project(":codegen"))
    api(project(":contract:abi"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.6.1")
}

publishing(createGPRPublisher { artifactId = "contract-generator" })
