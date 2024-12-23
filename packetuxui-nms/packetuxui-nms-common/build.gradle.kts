plugins {
    `common-conventions`
    `shadow-conventions`
}

dependencies {
    compileOnlyApi(project(":packetuxui-api"))
}