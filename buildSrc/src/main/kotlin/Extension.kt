import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

fun Project.createGPRPublisher(configuration: MavenPublication.() -> Unit): PublishingExtension.() -> Unit {
    return {
        repositories {
            maven {
                name = "GitHubPackages"
                setUrl("https://maven.pkg.github.com/jyc228/keth-client")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
        publications {
            create<MavenPublication>("gpr") {
                this.from(components["java"])
                this.groupId = "com.github.jyc228.keth"
                this.version = project.version.toString()
                this.configuration()
            }
        }
    }
}
