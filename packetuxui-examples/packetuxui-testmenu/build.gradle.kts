import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `common-conventions`
    `bukkit-conventions`
    `shadow-conventions`
}

dependencies {
    compileOnlyApi(project(":packetuxui-bukkit"))
}

tasks.runServer {
    pluginJars.from(
        project(":packetuxui-bukkit").tasks.named(
            "shadowJar"
        )
    )
}

paper {
    main = "net.craftoriya.TestMenu"

    serverDependencies {
        register("packetuxui-bukkit") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
    }
}


