import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
//import gradle.kotlin.dsl.accessors._88633a8ea7363b6fccbeefdddd6a70fa.publishing
import org.gradle.kotlin.dsl.withType



plugins {
    `common-conventions`
    //`shadow-conventions`
    `maven-publish`
    kotlin("kapt")
    id("com.github.johnrengelman.shadow")
    id("eclipse")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
    id("xyz.jpenilla.run-velocity")
    id("com.gradleup.shadow")

}

tasks.withType<ShadowJar> {
    mergeServiceFiles()

    //exclude("kotlin/**")
    val group = "dev.slne.surf.gui"
    val relocations = mapOf(
        "com.github.shynixn.mccoroutine" to "$group.libs.mccoroutine",
        "org.intellij" to "$group.libs.intellij",
        "org.jetbrains" to "$group.libs.jetbrains",
        "it.unimi.dsi.fastutil" to "$group.libs.fastutil"
    )

    relocations.forEach { (from, to) ->
        relocate(from, to)
    }
}


repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }

    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-velocity:2.7.0")
    compileOnlyApi(libs.adventure.api)
    compileOnlyApi(libs.adventure.minimessage)

    implementation("it.unimi.dsi:fastutil:8.5.12")
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    kapt("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")



    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    //implementation("dev.jorel:commandapi-velocity-shade:9.5.0-SNAPSHOT")

    api(project(":surf-gui-api"))
}

tasks {
  runVelocity {
        // Configure the Velocity version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        velocityVersion("3.4.0-SNAPSHOT")

  }
}

tasks.shadowJar {
    archiveClassifier.set("")
    configurations = listOf(project.configurations.runtimeClasspath.get())

    relocate("kotlin", "dev.slne.surf.gui.shaded.kotlin")
    //relocate("com.github.retrooper", "dev.slne.surf.gui.shaded.retrooper")
    relocate("kotlinx.coroutines", "dev.slne.surf.gui.shaded.kotlinx.coroutines")

    //relocate("com.google.inject", "dev.slne.surf.gui.shaded.com.google.inject")
}

tasks.jar { enabled = false }

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

val templateSource = file("src/main/templates")
val templateDest = layout.buildDirectory.dir("generated/sources/templates")
val generateTemplates = tasks.register<Copy>("generateTemplates") {
    val props = mapOf("version" to project.version)
    inputs.properties(props)

    from(templateSource)
    into(templateDest)
    expand(props)
}

sourceSets.main.configure { java.srcDir(generateTemplates.map { it.outputs }) }

//project.idea.project.settings.taskTriggers.afterSync(generateTemplates)
project.eclipse.synchronizationTasks(generateTemplates)

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
