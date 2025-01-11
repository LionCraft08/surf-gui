package dev.slne.packetuxui.nms.common

import dev.slne.packetuxui.user.User

interface ContainerHelper {

    fun getNextContainerId(user: User): Int

    fun hasOpenedContainer(user: User): Boolean

}