import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `maven-publish`

    id("com.gradleup.shadow")
    id("org.hibernate.build.maven-repo-auth")
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()

    exclude("kotlin/**")
    val group = "dev.slne"
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
        if (version.toString().endsWith("-SNAPSHOT")) {
            maven("https://repo.slne.dev/repository/maven-snapshots") {
                name = "maven-snapshots"
                credentials {
                    username = System.getenv("SLNE_SNAPSHOTS_REPO_USERNAME")
                    password = System.getenv("SLNE_SNAPSHOTS_REPO_PASSWORD")
                }
            }
        } else {
            maven("https://repo.slne.dev/repository/maven-releases") {
                name = "maven-releases"
                credentials {
                    username = System.getenv("SLNE_RELEASES_REPO_USERNAME")
                    password = System.getenv("SLNE_RELEASES_REPO_PASSWORD")
                }
            }
        }
    }
}
