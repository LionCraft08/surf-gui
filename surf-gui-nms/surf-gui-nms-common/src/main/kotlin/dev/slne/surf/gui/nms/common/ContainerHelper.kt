package dev.slne.surf.gui.nms.common

import dev.slne.surf.gui.user.User

interface ContainerHelper {

    /**
     * Gets the next container id for the user.
     */
    fun getNextContainerId(user: User): Int

    /**
     * Checks if the user has an opened container.
     */
    fun hasOpenedContainer(user: User): Boolean

}