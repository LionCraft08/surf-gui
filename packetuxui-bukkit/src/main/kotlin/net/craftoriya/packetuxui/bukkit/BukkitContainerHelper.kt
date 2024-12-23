package net.craftoriya.packetuxui.bukkit

import net.craftoriya.packetuxui.nms.v1_21_1.ContainerHelper1_21_4
import net.craftoriya.packetuxui.user.User
import org.bukkit.Bukkit

object BukkitContainerHelper {

    fun getNextContainerId(user: User): Int {
        val serverVersion = Bukkit.getMinecraftVersion()
        println("Server version: $serverVersion")

        return when {
            serverVersion.contains("1.21.1") -> ContainerHelper1_21_4.getNextContainerId(user)
            else -> error("Unsupported server version: $serverVersion")
        }
    }

}