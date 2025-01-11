package net.craftoriya.packetuxui.bukkit

import net.craftoriya.packetuxui.PacketUxUiApi
import net.craftoriya.packetuxui.bukkit.user.BukkitUser
import net.craftoriya.packetuxui.user.User
import java.util.*

object PacketUxUiBukkitApi : PacketUxUiApi() {

    init {
        setInstance(this)
    }

    override fun createNewUser(uuid: UUID) = BukkitUser(uuid)

    override suspend fun getNextContainerId(user: User) =
        BukkitContainerHelper.getNextContainerId(user)

    override suspend fun hasOpenedContainer(user: User) =
        BukkitContainerHelper.hasOpenedContainer(user)
}