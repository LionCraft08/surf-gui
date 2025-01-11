plugins {
    `java-library`
}

val libs: VersionCatalog = the<VersionCatalogsExtension>().named("libs")
dependencies {
    compileOnly(libs.findLibrary("paper").orElseThrow())
    compileOnly(libs.findLibrary("commandapi-bukkit").orElseThrow())
    compileOnly(libs.findLibrary("commandapi-bukkit-kotlin").orElseThrow())

    implementation(libs.findLibrary("mccoroutine-folia").orElseThrow())
    implementation(libs.findLibrary("mccoroutine-folia-core").orElseThrow())
}