package dev.slne.surf.gui.user

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenBook
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow
import dev.slne.surf.gui.common.mutableObjectListOf
import dev.slne.surf.gui.menu.menu.Menu
import java.util.*
import kotlin.collections.ArrayList

abstract class AbstractUser(
    override val uuid: UUID
) : User {

    private var activeMenu: Menu? = null
    private var menuTree:List<String> = mutableObjectListOf()

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

    override fun closeCurrentMenu() {
        getActiveMenu()?.close(this)
    }

}