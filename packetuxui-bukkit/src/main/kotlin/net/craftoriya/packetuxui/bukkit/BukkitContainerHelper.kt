package net.craftoriya.packetuxui.bukkit

import net.craftoriya.packetuxui.nms.common.ContainerHelper
import net.craftoriya.packetuxui.nms.v1_21_1.ContainerHelper1_21_4
import net.craftoriya.packetuxui.user.User
import org.bukkit.Bukkit

object BukkitContainerHelper {

    private fun getContainerHelper(): ContainerHelper {
        val serverVersion = Bukkit.getMinecraftVersion()

        return when {
            serverVersion.contains("1.21.1") -> ContainerHelper1_21_4
            else -> error("Unsupported server version: $serverVersion")
        }
    }

    fun getNextContainerId(user: User): Int {
        return getContainerHelper().getNextContainerId(user)
    }

    fun hasOpenedContainer(user: User): Boolean {
        return getContainerHelper().hasOpenedContainer(user)
    }

}