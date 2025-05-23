package dev.slne.surf.gui.bukkit.user

import com.github.retrooper.packetevents.PacketEvents
import dev.slne.surf.gui.menu.menu.BookProvider
import dev.slne.surf.gui.bukkit.extensions.toPlayer
import dev.slne.surf.gui.communication.CommunicationHandler
import dev.slne.surf.gui.user.AbstractUser
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.*
import com.github.retrooper.packetevents.protocol.player.User as PacketUser

class BukkitUser(override val uuid: UUID) : AbstractUser(uuid) {

    override fun updateInventory() {
        player?.updateInventory()
    }

    override fun openBook(book: Book) {
        player!!.openBook(book)
    }

    override fun openPreviousMenu() {
        CommunicationHandler.sendInventoryStackCommand(uuid, "open_previous")
    }

    override fun addOpenedMenu(id: String) {
        CommunicationHandler.sendInventoryStackCommand(uuid, "open:$id")
    }

    override val packetUser: PacketUser?
        get() = player?.let { PacketEvents.getAPI().playerManager.getUser(it) }

    override val player: Player?
        get() = toPlayer()

    override fun sendMessage(message: Component) {
        player?.sendMessage(message)?:println("Error 1")
    }

    override fun getNewContainerID(): Int {
        return getCurrentContainerID()+1
    }

}