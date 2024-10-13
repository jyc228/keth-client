plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.0.21"
    `maven-publish`
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.6.1")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
}

publishing(createGPRPublisher { artifactId = "contract-abi" })
