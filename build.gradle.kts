plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    `maven-publish`
}

allprojects {
    apply(plugin = "kotlin")

    group = "com.github.jyc228"

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation(kotlin("test"))
        testImplementation("io.kotest:kotest-runner-junit5:5.8.1")
        testImplementation("io.kotest:kotest-runner-junit5:5.8.1")
        testImplementation("io.kotest:kotest-assertions-core:5.8.1")
        testImplementation("io.kotest:kotest-assertions-json:5.8.1")
        testImplementation("io.kotest:kotest-framework-datatest:5.8.1")
    }

    kotlin {
        jvmToolchain(17)
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    tasks.test {
        useJUnitPlatform()
    }
}

dependencies {
    api(project(":contract:abi"))

    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

    implementation("com.auth0:java-jwt:4.4.0")

    val jacksonVersion = "2.14.1"
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.web3j:core:4.10.0")

    val ktorVersion = "2.1.2"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}
