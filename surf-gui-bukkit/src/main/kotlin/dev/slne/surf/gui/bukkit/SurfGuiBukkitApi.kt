package dev.slne.surf.gui.bukkit

import dev.slne.surf.gui.SurfGuiApi
import dev.slne.surf.gui.SurfGuiBukkitPlugin
import dev.slne.surf.gui.bukkit.user.BukkitUser
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Server
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.io.File
import java.util.*

object SurfGuiBukkitApi : SurfGuiApi() {
    private var logger = LoggerFactory.getLogger("surf-gui").apply { atLevel(Level.DEBUG)}

    init {
        setInstance(this, true)
    }

    override fun createNewUser(uuid: UUID) = BukkitUser(uuid)
// seems not necessary
//    override suspend fun getNextContainerId(user: User) =
//        BukkitContainerHelper.getNextContainerId(user)
//
//    override suspend fun hasOpenedContainer(user: User) =
//        BukkitContainerHelper.hasOpenedContainer(user)

    fun getLogger(): Logger {
        return logger
    }

    override suspend fun sendMenuMessage(playername: String, channel: String, data: ByteArray) {
        val p = Bukkit.getPlayer(UUID.fromString(playername))
        if (p == null) throw NullPointerException("Could not find the Player named $playername")
        p.sendPluginMessage(SurfGuiBukkitPlugin.instance(), channel, data)
    }

    override fun getDataFile(): File {
        return SurfGuiBukkitPlugin.instance().dataFolder
    }

    override fun log(message: String, level: System.Logger.Level) {
        if (System.Logger.Level.valueOf(SurfGuiBukkitPlugin.config().getString("logger-level", "INFO")!!).severity >= level.severity){
            Component.text(
                "["+SurfGuiBukkitPlugin.instance().name+"] "+ message, when (level){
                System.Logger.Level.WARNING -> TextColor.color(250, 160, 0)
                System.Logger.Level.ERROR -> TextColor.color(200, 40, 0)
                System.Logger.Level.DEBUG -> TextColor.color(0, 255, 255)
                else -> TextColor.color(255, 255, 255)
            })
            Bukkit.getConsoleSender().sendMessage(message)
        }
    }
}