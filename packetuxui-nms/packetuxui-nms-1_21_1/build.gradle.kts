plugins {
    `common-conventions`
    `shadow-conventions`
    `nms-conventions`
}

dependencies {
    api(project(":packetuxui-nms:packetuxui-nms-common"))

    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
}