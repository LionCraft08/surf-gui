import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `java-library`

    id("net.minecrell.plugin-yml.paper")
    id("xyz.jpenilla.run-paper")
}

tasks.runServer {
    minecraftVersion("1.21.1")

    downloadPlugins {
        modrinth("packetevents", "2.7.0")
        modrinth("commandapi", "9.7.0")
    }
}

paper {
    name = project.name
    version = rootProject.findProperty("version") as String? ?: "undefined"

    foliaSupported = true
    apiVersion = "1.21"
    authors = listOf("OceJlot", "Ammo", "Twisti_twixi")
    generateLibrariesJson = true

    serverDependencies {
        register("packetevents") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
        register("CommandAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
            joinClasspath = true
        }
    }
}