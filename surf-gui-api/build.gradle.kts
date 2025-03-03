plugins {
    `common-conventions`
    `shadow-conventions`
}

dependencies {
    compileOnly(libs.packetevents.api)
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.minimessage)
    compileOnly(libs.adventure.plaintext)

    compileOnlyApi(libs.fastutil)
    compileOnlyApi(libs.coroutines)
    compileOnlyApi(libs.okhttp)
    compileOnlyApi(libs.okhttp.kotlin)
    compileOnlyApi(libs.caffeine.courotines)
    compileOnlyApi(libs.gson)
}
