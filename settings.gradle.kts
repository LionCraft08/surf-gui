plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "surf-gui"

// Root
include("surf-gui-api")
include("surf-gui-bukkit")
include("surf-gui-velocity")

// NMS
include("surf-gui-nms:surf-gui-nms-common")
include("surf-gui-nms:surf-gui-nms-1_21_1")
include("surf-gui-nms:surf-gui-nms-1_21_4")

// Examples
//include("surf-gui-examples:surf-gui-testmenu")