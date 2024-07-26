plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.github.jyc228"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
