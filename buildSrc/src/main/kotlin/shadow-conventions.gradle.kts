import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.util.Properties

plugins {
    `maven-publish`

    id("com.gradleup.shadow")
    id("org.hibernate.build.maven-repo-auth")
}

val customProperties = Properties().apply {
    rootProject.file("repo.properties").takeIf { it.exists() }?.reader()?.use { load(it) }
}

val mavenRepoUrl: String = customProperties["mavenSnapshotRepoUrl"]?.toString()
    ?: error("Missing mavenSnapshotRepoUrl")
val mavenRepoName: String = customProperties["mavenSnapshotRepoName"]?.toString()
    ?: error("Missing mavenSnapshotRepoName")

tasks.withType<ShadowJar> {
    mergeServiceFiles()

    exclude("kotlin/**")
    val group = "net.craftoriya"
    val relocations = mapOf(
        "com.github.shynixn.mccoroutine" to "$group.libs.mccoroutine",
        "org.intellij" to "$group.libs.intellij",
        "org.jetbrains" to "$group.libs.jetbrains"
    )

    relocations.forEach { (from, to) ->
        relocate(from, to)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
        maven(mavenRepoUrl) {
            name = mavenRepoName
        }
    }
}
