plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.0.0"
    `maven-publish`
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.6.1")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
}

publishing(createGPRPublisher { artifactId = "contract-abi" })
