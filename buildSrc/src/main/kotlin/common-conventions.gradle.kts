plugins {
    kotlin("jvm")

    `java-library`
}

repositories {
    mavenCentral()

    maven("https://repo.slne.dev/repository/maven-public") { name = "maven-public" }
}

group = "dev.slne.surf"
version = findProperty("version") as String

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

kotlin {
    jvmToolchain(21)
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}