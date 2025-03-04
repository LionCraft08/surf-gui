import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `java-library`
}

val libs = the<LibrariesForLibs>()

dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.commandapi.bukkit)
    compileOnly(libs.commandapi.bukkit.kotlin)

    implementation(libs.mccoroutine.folia)
    implementation(libs.mccoroutine.folia.core)
}