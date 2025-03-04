package dev.slne.surf.gui.nms.v1_21_1

import dev.slne.surf.gui.nms.common.ContainerHelper
import dev.slne.surf.gui.user.User
import org.bukkit.Bukkit

@Suppress("ClassName")
object ContainerHelper_1_21_1 : ContainerHelper {

    override fun getNextContainerId(user: User) =
        Bukkit.getPlayer(user.uuid)?.toNms()?.nextContainerCounter() ?: -1

    override fun hasOpenedContainer(user: User) =
        Bukkit.getPlayer(user.uuid)?.toNms()?.hasContainerOpen() == true
}