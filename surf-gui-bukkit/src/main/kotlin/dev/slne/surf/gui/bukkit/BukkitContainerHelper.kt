package dev.slne.surf.gui.bukkit

import dev.slne.surf.gui.nms.v1_21_1.ContainerHelper_1_21_1
import dev.slne.surf.gui.nms.v1_21_4.ContainerHelper_1_21_4
import dev.slne.surf.gui.user.User
import org.bukkit.Bukkit

object BukkitContainerHelper {

    private val containerHelper by lazy {
        val v = Bukkit.getMinecraftVersion()
        when {
            v.contains("1.21.1") -> ContainerHelper_1_21_1
            v.contains("1.21.4") -> ContainerHelper_1_21_4
            else -> error("Unsupported server version: $v")
        }
    }

    fun getNextContainerId(user: User): Int {
        return containerHelper.getNextContainerId(user)
    }

    fun hasOpenedContainer(user: User): Boolean {
        return containerHelper.hasOpenedContainer(user)
    }

}