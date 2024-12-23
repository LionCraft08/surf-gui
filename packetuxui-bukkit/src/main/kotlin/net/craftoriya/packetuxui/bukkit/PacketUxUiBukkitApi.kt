package net.craftoriya.packetuxui.bukkit

import net.craftoriya.packetuxui.PacketUxUiApi
import net.craftoriya.packetuxui.user.User

object PacketUxUiBukkitApi : PacketUxUiApi() {

    override suspend fun getNextContainerId(user: User): Int {
        return BukkitContainerHelper.getNextContainerId(user)
    }
}