import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `common-conventions`
    `bukkit-conventions`
    `shadow-conventions`
    `bukkit-server-conventions`
}

dependencies {
    compileOnlyApi(project(":surf-gui-bukkit"))
}

tasks.runServer {
    pluginJars.from(
        project(":surf-gui-bukkit").tasks.named(
            "shadowJar"
        )
    )
}

paper {
    main = "dev.slne.surf.gui.example.TestMenu"

    serverDependencies {
        register("surf-gui-bukkit") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
    }
}


