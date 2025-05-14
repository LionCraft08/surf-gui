package dev.slne.surf.gui.velocity;

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.gui.SurfGuiApi
import dev.slne.surf.gui.velocity.commands.MenuCommand
import dev.slne.surf.gui.velocity.commands.menuSubCommands.OpenSpecificCommand
import dev.slne.surf.gui.velocity.eventlistener.PlayerConnectionListeners
import dev.slne.surf.gui.velocity.eventlistener.PluginMessageReceiver
import org.slf4j.Logger
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path


@Plugin(
    id = "surf-gui-velocity",
    name = "surf-gui-velocity",
    version = "1.0"
    ,description = "The Velocity implementation of SurfGUI"
    ,authors = ["LionCraft"]
)
class SurfGuiVelocityPlugin @Inject constructor(val logger: Logger, val server: ProxyServer, @DataDirectory val dataDirectory: Path) {
    init {
        instance = this
        logger.info("Created Proxy-Side Plugin Surf-GUI")
    }


    @Subscribe
    fun onProxyInitialization(e: ProxyInitializeEvent) {
        //PacketEvents.setAPI(VelocityPacketEventsBuilder.build(server, server.pluginManager.ensurePluginContainer(this), logger,@DataDirectory dataDirectory))
        saveFile()

        SurfGuiApi.setInstance(SurfGUIVelocityAPI, false)
        SurfGuiApi.getInstance().init()

        //Plugin-Messaging
        server.channelRegistrar.register(PluginMessageReceiver.menu_open_requests)
        server.channelRegistrar.register(PluginMessageReceiver.menu_commands)


        //Listeners
        server.eventManager.register(this, PluginMessageReceiver())
        server.eventManager.register(this, PlayerConnectionListeners())

        //Commands
        server.commandManager.register(server.commandManager.metaBuilder("debug-menu").plugin(this).build(),
            OpenSpecificCommand())

        server.commandManager.register(server.commandManager.metaBuilder("menu").aliases("velocity-menu").plugin(this).build(), MenuCommand())

        logger.debug("Initialized Proxy-Side Plugin Surf-GUI")
    }
    @Subscribe
    fun onShutdown(e: ProxyShutdownEvent){
        SurfGuiApi.getInstance().terminate()
    }

    companion object{
        private lateinit var instance: SurfGuiVelocityPlugin
        fun getInstance(): SurfGuiVelocityPlugin = instance
    }

    fun saveFile(){
        if (!dataDirectory.toFile().exists()) dataDirectory.toFile().mkdirs()
        if (!dataDirectory.resolve("overlays.json").toFile().exists()) {
            saveResource("overlays.json", dataDirectory.resolve("overlays.json").toFile())
        }
    }
    fun saveResource(name: String) {
        saveResource(name, dataDirectory.resolve(name).toFile())
    }
    fun saveResource(name: String, targetFile: File) {
        val inputStream=SurfGuiApi::class.java.classLoader.getResourceAsStream(name)
        if (!targetFile.exists()) {
            targetFile.createNewFile()
        }
        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(targetFile)

            val buffer = ByteArray(4096)
            var bytesRead: Int
            if (inputStream != null) {
                while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            } else println("Resource $name not found")

        } catch (e: IOException) {
            System.err.println("Error saving resource: " + e.message)
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                System.err.println("Error closing input stream: " + e.message)
            }
            try {
                outputStream?.close()
            } catch (e: IOException) {
                System.err.println("Error closing output stream: " + e.message)
            }
        }
    }
}
