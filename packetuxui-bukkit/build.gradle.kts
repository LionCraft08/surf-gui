plugins {
    `common-conventions`
    `shadow-conventions`
    `bukkit-conventions`
    `bukkit-server-conventions`
}

paper {
    main = "dev.slne.packetuxui.PacketUxUiPlugin"
    loader = "dev.slne.packetuxui.bukkit.PacketUxUiBukkitLoader"
}

dependencies {
    compileOnlyApi(libs.paper)
    compileOnlyApi(libs.packetevents.bukkit)

    api(project(":packetuxui-api"))

    paperLibrary(libs.fastutil)
    paperLibrary(libs.coroutines)
    paperLibrary(libs.okhttp)
    paperLibrary(libs.okhttp.kotlin)
    paperLibrary(libs.caffeine.courotines)
    paperLibrary(libs.gson)

    // NMS
    api(project(":packetuxui-nms:packetuxui-nms-1_21_1"))
}