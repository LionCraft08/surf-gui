plugins {
    `common-conventions`
    `shadow-conventions`
}

dependencies {
    compileOnly(libs.packetevents.api)
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.minimessage)
    compileOnly(libs.adventure.plaintext)

    implementation("org.slf4j:slf4j-api:2.0.17")

    compileOnlyApi(libs.fastutil)
    compileOnlyApi(libs.coroutines)
    compileOnlyApi(libs.okhttp)
    compileOnlyApi(libs.okhttp.kotlin)
    compileOnlyApi(libs.caffeine.courotines)
    compileOnlyApi(libs.gson)
}
