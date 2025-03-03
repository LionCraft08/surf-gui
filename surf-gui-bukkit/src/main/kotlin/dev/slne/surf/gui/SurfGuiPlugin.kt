package dev.slne.surf.gui

import com.github.retrooper.packetevents.PacketEvents
import dev.slne.surf.gui.bukkit.SurfGuiBukkitApi
import dev.slne.surf.gui.bukkit.commands.SurfGuiCommand
import dev.slne.surf.gui.bukkit.controller.BukkitListener
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

internal val plugin get() = JavaPlugin.getPlugin(SurfGuiPlugin::class.java)

class SurfGuiPlugin : JavaPlugin() {

    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
    }

    override fun onEnable() {
        SurfGuiBukkitApi.init()

        // Listeners
        Bukkit.getPluginManager().registerEvents(BukkitListener, plugin)

        // Commands
        SurfGuiCommand
    }

    override fun onDisable() {
        SurfGuiBukkitApi.terminate()
    }
}