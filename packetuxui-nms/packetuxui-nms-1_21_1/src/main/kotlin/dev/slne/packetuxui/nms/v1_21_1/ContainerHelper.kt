package dev.slne.packetuxui.nms.v1_21_1

import dev.slne.packetuxui.nms.common.ContainerHelper
import dev.slne.packetuxui.user.User
import org.bukkit.Bukkit

@Suppress("ClassName")
object ContainerHelper1_21_1 : ContainerHelper {

    override fun getNextContainerId(user: User) =
        Bukkit.getPlayer(user.uuid)?.toNms()?.nextContainerCounter() ?: -1

    override fun hasOpenedContainer(user: User) =
        Bukkit.getPlayer(user.uuid)?.toNms()?.hasContainerOpen() == true
}