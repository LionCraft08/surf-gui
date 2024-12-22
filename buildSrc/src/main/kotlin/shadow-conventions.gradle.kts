import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `maven-publish`

    id("com.gradleup.shadow")
    id("org.hibernate.build.maven-repo-auth")
}

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
        maven(System.getenv("MAVEN_SNAPSHOT_REPOSITORY_URL")) {
            name = System.getenv("MAVEN_SNAPSHOT_REPOSITORY_NAME")
        }
    }
}
