import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

fun Project.createGPRPublisher(configuration: MavenPublication.() -> Unit): PublishingExtension.() -> Unit {
    return {
        repositories { gprKethClient() }
        publications {
            create<MavenPublication>("gpr") {
                this.from(components["java"])
                this.groupId = "com.github.jyc228"
                this.version = project.version.toString()
                this.configuration()
            }
        }
    }
}

fun RepositoryHandler.gprKethClient(): MavenArtifactRepository = maven {
    name = "GitHubPackages"
    setUrl("https://maven.pkg.github.com/jyc228/keth-client")
    credentials {
        username = System.getenv("GITHUB_ACTOR")
        password = System.getenv("GITHUB_TOKEN")
    }
}
