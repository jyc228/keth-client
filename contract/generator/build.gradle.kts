plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.1.0"
    `maven-publish`
}

dependencies {
    api(project(":codegen"))
    api(project(":contract:abi"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.6.1")
}

publishing(createGPRPublisher { artifactId = "contract-generator" })

tasks.register<JavaExec>("generateContractWrapperToRootProject") {
    mainClass.set("com.github.jyc228.keth.client.contract.library.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf(rootProject.sourceSets.main.get().kotlin.srcDirs.first().path)
}