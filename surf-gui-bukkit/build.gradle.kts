import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `common-conventions`
    `shadow-conventions`
    `bukkit-conventions`
    `bukkit-server-conventions`
}

paper {
    main = "dev.slne.surf.gui.SurfGuiBukkitPlugin"
    loader = "dev.slne.surf.gui.bukkit.SurfGuiBukkitLoader"
    description = "A Bukkit plugin for SurfGUI."
//    serverDependencies{
//        register("SurfFriends") {
//            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
//            required = false
//            joinClasspath = true
//        }
//    }
}

dependencies {
    compileOnlyApi(libs.paper)
    compileOnlyApi(libs.packetevents.bukkit)

    api(project(":surf-gui-api"))

    paperLibrary(libs.fastutil)
    paperLibrary(libs.coroutines)
    paperLibrary(libs.okhttp)
    paperLibrary(libs.okhttp.kotlin)
    paperLibrary(libs.caffeine.courotines)
    paperLibrary(libs.gson)

    // NMS
    //api(project(":surf-gui-nms:surf-gui-nms-1_21_1"))
    //api(project(":surf-gui-nms:surf-gui-nms-1_21_4"))
}
tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("C:\\Users\\Anwender\\IdeaProjects\\surf-social\\surf-friends\\run\\plugins"))
}