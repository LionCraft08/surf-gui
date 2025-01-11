plugins {
    kotlin("jvm")

    `java-library`
}

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
}

group = "net.craftoriya"
version = "1.0.1-SNAPSHOT"

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