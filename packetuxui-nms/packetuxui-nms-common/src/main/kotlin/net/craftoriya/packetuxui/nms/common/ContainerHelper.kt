package net.craftoriya.packetuxui.nms.common

import net.craftoriya.packetuxui.user.User

interface ContainerHelper {

    fun getNextContainerId(user: User): Int

    fun hasOpenedContainer(user: User): Boolean

}