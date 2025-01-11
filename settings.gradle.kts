plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "PacketUxUi"

// Root
include("packetuxui-api")
include("packetuxui-bukkit")

// NMS
include("packetuxui-nms:packetuxui-nms-common")
include("packetuxui-nms:packetuxui-nms-1_21_1")

// Examples
include("packetuxui-examples:packetuxui-testmenu")