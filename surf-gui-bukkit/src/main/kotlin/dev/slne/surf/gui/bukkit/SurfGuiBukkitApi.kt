package dev.slne.surf.gui.bukkit

import dev.slne.surf.gui.SurfGuiApi
import dev.slne.surf.gui.bukkit.user.BukkitUser
import dev.slne.surf.gui.user.User
import java.util.*

object SurfGuiBukkitApi : SurfGuiApi() {

    init {
        setInstance(this)
    }

    override fun createNewUser(uuid: UUID) = BukkitUser(uuid)

    override suspend fun getNextContainerId(user: User) =
        BukkitContainerHelper.getNextContainerId(user)

    override suspend fun hasOpenedContainer(user: User) =
        BukkitContainerHelper.hasOpenedContainer(user)
}