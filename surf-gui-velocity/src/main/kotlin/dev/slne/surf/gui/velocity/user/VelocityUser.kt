package dev.slne.surf.gui.velocity.user

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.player.User
import com.velocitypowered.api.proxy.Player
import dev.slne.surf.gui.communication.CommunicationHandler
import dev.slne.surf.gui.menu.menu.MenuService
import dev.slne.surf.gui.user.AbstractUser
import dev.slne.surf.gui.velocity.SurfGuiVelocityPlugin
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import java.util.UUID

class VelocityUser(override val uuid: UUID) : AbstractUser(uuid){
    val inventoryStackTrace = mutableListOf<String>()
    override val packetUser: User?
        get() = player?.let { PacketEvents.getAPI().playerManager.getUser(it) }
    override val player: Player?
        get() = toPlayer()

    override fun updateInventory() {
        //TODO("Not yet implemented")
    }

    override fun openPreviousMenu() {
        if (inventoryStackTrace.isNotEmpty())
        inventoryStackTrace.removeLast()
        if (inventoryStackTrace.isEmpty()) closeCurrentMenu()
        else MenuService.openMenu(this, inventoryStackTrace.last())
    }

    override fun closeCurrentMenu() {
        super.closeCurrentMenu()
        inventoryStackTrace.clear()
    }

    override fun openBook(book: String) {
        CommunicationHandler.sendInventoryRequest(uuid, "book:$book")
    }

    override fun addOpenedMenu(id: String) {
        //Checks if an opened Menu already exists and removes every Menu opened after it if it does
        //Could be removed for a different user experience
        if (inventoryStackTrace.contains(id)){
            val id = inventoryStackTrace.indexOf(id)
            inventoryStackTrace.removeIf { s -> inventoryStackTrace.indexOf(s) >= id}
        }
        //until here ^^

        inventoryStackTrace.add(id)
    }

    fun toPlayer(): Player? = SurfGuiVelocityPlugin.getInstance().server.getPlayer(uuid)?.get()

    override fun sendMessage(message: Component) {
        player?.sendMessage(message)
    }

}