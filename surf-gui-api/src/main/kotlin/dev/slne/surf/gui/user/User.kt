package dev.slne.surf.gui.user

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import dev.slne.surf.gui.menu.menu.Menu
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import java.util.*
import com.github.retrooper.packetevents.protocol.player.User as PacketUser

interface User {

    /**
     * The UUID of the player
     */
    val uuid: UUID

    /**
     * The PacketUser of the player coming from PacketEvents
     */
    val packetUser: PacketUser?

    /**
     * The player object either velocity or paper
     */
    val player: Any?

    /**
     * The active menu of the player
     *
     * @return The active menu of the player
     */
    fun getActiveMenu(): Menu?

    /**
     * Updates the inventory of the player
     */
    fun updateInventory()

    /**Closes the player's current open Menu (if available)
     *
     */
    fun closeCurrentMenu()

    fun openBook(book: Book)

    /**
     * Uses stacked Menus to track the order of the opened Menus.
     * The Stack resets if a User closes a Menu.
     */
    fun openPreviousMenu()

    /**
     * Adds a menu to the Stack Trace
     */
    fun addOpenedMenu(id: String)

    /**
     * Sends a packet to the player
     *
     * @param wrapper The packet wrapper to send
     */
    fun sendPacket(wrapper: PacketWrapper<*>) = this.packetUser?.sendPacket(wrapper)

    /**
     * Receives a packet from the player
     *
     * @param wrapper The packet wrapper to receive
     */
    fun receivePacket(wrapper: PacketWrapper<*>) =
        PacketEvents.getAPI().playerManager.receivePacketSilently(player, wrapper)

    /**
     * Sends a message to the player
     * @param message The message to send
     */
    fun sendMessage(message: Component)

    fun getCurrentContainerID():Int

    fun getNextContainerID():Int
}