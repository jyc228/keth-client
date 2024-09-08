plugins {
    kotlin("jvm")
    `maven-publish`
}

publishing(createGPRPublisher { artifactId = "codegen" })
