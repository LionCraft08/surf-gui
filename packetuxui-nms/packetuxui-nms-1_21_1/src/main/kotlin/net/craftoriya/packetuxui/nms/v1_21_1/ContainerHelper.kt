package net.craftoriya.packetuxui.nms.v1_21_1

import net.craftoriya.packetuxui.nms.common.ContainerHelper
import net.craftoriya.packetuxui.user.User
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.entity.CraftPlayer

object ContainerHelper1_21_4 : ContainerHelper {

    override fun getNextContainerId(user: User): Int {
        val player = Bukkit.getPlayer(user.uuid) ?: return -1
        val craftPlayer = player as CraftPlayer

        return craftPlayer.handle.nextContainerCounter()
    }

    override fun hasOpenedContainer(user: User): Boolean {
        val player = Bukkit.getPlayer(user.uuid) ?: return false
        val craftPlayer = player as CraftPlayer

        return craftPlayer.handle.hasContainerOpen()
    }

}