package dev.slne.packetuxui

import com.github.retrooper.packetevents.PacketEvents
import dev.slne.packetuxui.bukkit.PacketUxUiBukkitApi
import dev.slne.packetuxui.bukkit.commands.PacketUxUiCommand
import dev.slne.packetuxui.bukkit.controller.BukkitListener
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

internal val plugin get() = JavaPlugin.getPlugin(PacketUxUiPlugin::class.java)

class PacketUxUiPlugin : JavaPlugin() {

    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
    }

    override fun onEnable() {
        PacketUxUiBukkitApi.init()

        // Listeners
        Bukkit.getPluginManager().registerEvents(BukkitListener, plugin)

        // Commands
        PacketUxUiCommand
    }

    override fun onDisable() {
        PacketUxUiBukkitApi.terminate()
    }
}