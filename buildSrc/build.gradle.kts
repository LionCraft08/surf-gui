plugins {
    `kotlin-dsl`
    idea

}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    implementation(libs.kotlin.jvm)
    implementation(libs.shadow.jar)
    implementation(libs.repoauth)

    implementation(libs.run.paper)
    implementation(libs.run.velocity)
    implementation(libs.plugin.yml)
    //implementation(libs.paper.userdev)
}