import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `maven-publish`

    id("com.gradleup.shadow")
    id("org.hibernate.build.maven-repo-auth")
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()

    val base = "net.craftoriya.libs"
    val relocations = listOf(
        "com.github.shynixn.mccoroutine" to "$base.mccoroutine",
        "io.leangen.geantyref" to "$base.geantyref",
        "kotlin" to "$base.kotlin",
        "kotlinx" to "$base.kotlinx",
        "org.incendo.cloud" to "$base.cloud",
        "org.intellij" to "$base.intellij",
        "org.jetbrains" to "$base.jetbrains",
        "okhttp3" to "$base.okhttp3",
        "com.sksamuel.aedile" to "$base.caffeine",
        "com.google.gson" to "$base.gson",
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
