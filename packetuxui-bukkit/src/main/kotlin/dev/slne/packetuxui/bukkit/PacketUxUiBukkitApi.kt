package dev.slne.packetuxui.bukkit

import dev.slne.packetuxui.PacketUxUiApi
import dev.slne.packetuxui.bukkit.user.BukkitUser
import dev.slne.packetuxui.user.User
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