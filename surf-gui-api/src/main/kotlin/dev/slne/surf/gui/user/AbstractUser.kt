package dev.slne.surf.gui.user

import dev.slne.surf.gui.menu.menu.Menu
import java.util.*

abstract class AbstractUser(
    override val uuid: UUID
) : User {

    private var activeMenu: Menu? = null

    override fun getActiveMenu() = activeMenu

    /**
     * Sets the active menu for this user.
     *
     * @param menu The menu to set as active.
     */
    fun setActiveMenu(menu: Menu?) {
        activeMenu = menu
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractUser

        return uuid == other.uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun toString(): String {
        return "AbstractUser(uuid=$uuid)"
    }

}