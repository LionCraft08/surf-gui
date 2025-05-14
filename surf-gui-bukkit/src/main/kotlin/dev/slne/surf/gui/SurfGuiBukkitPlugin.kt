package dev.slne.surf.gui

import com.github.retrooper.packetevents.PacketEvents
import dev.slne.surf.gui.bukkit.SurfGuiBukkitApi
import dev.slne.surf.gui.bukkit.books.BookProvider
import dev.slne.surf.gui.bukkit.commands.SurfGuiCommand
import dev.slne.surf.gui.bukkit.controller.BukkitListener
import dev.slne.surf.gui.bukkit.controller.PluginMessageReceiver
import dev.slne.surf.gui.communication.CommunicationHandler
import dev.slne.surf.gui.menu.menu.MenuService
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

internal val plugin get() = JavaPlugin.getPlugin(SurfGuiBukkitPlugin::class.java)


class SurfGuiBukkitPlugin : JavaPlugin() {

    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        instance = this

    }

    override fun onEnable() {
        SurfGuiBukkitApi.init()
        saveDefaultConfig()
        saveResource("overlays.json", false)
        configuration = config


        // Listeners
        Bukkit.getPluginManager().registerEvents(BukkitListener, plugin)
        server.messenger.registerIncomingPluginChannel(this, CommunicationHandler.getChannelOpen(), PluginMessageReceiver())
        server.messenger.registerOutgoingPluginChannel(this, CommunicationHandler.getChannelOpen())
        server.messenger.registerIncomingPluginChannel(this, CommunicationHandler.getChannelCommands(), PluginMessageReceiver())
        server.messenger.registerOutgoingPluginChannel(this, CommunicationHandler.getChannelCommands())

        // Commands
        SurfGuiCommand
    }

    override fun onDisable() {
        SurfGuiBukkitApi.terminate()
    }
    companion object{
        private lateinit var instance: JavaPlugin
        private lateinit var configuration: FileConfiguration
        fun instance(): JavaPlugin = instance
        fun config(): FileConfiguration = configuration
    }
}