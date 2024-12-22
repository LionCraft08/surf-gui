plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.jvm)
    implementation(libs.shadow.jar)
    implementation(libs.repoauth)

    implementation(libs.run.paper)
    implementation(libs.run.velocity)
    implementation(libs.plugin.yml)
}