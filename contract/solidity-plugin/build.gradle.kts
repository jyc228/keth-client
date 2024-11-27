plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.1.0"
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

dependencies {
    implementation(project(":contract:generator"))
    implementation(gradleKotlinDsl())
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    testImplementation(gradleTestKit())
}

gradlePlugin {
    plugins {
        create("solidity-plugin") {
            id = "com.github.jyc228.keth"
            implementationClass = "com.github.jyc228.keth.solidity.SolidityPlugin"
        }
    }
}

publishing { repositories { gprKethClient() } }
