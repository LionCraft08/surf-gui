package dev.slne.surf.gui.communication

import dev.slne.surf.gui.SurfGuiApi
import dev.slne.surf.gui.common.toComponent
import dev.slne.surf.gui.common.toPlain
import dev.slne.surf.gui.menu.menu.MenuService
import dev.slne.surf.gui.user.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.kyori.adventure.inventory.Book
import java.nio.charset.StandardCharsets
import java.util.UUID

object CommunicationHandler {
    private const val CHANNEL_OPEN = "surf_gui:menu_open_requests"
    private const val CHANNEL_COMMANDS = "surf_gui:menu_commands"
    private val scope = CoroutineScope(Dispatchers.Default)

    fun sendInventoryRequest(uuid: UUID, id: String){
        scope.launch {
            SurfGuiApi.getInstance().sendMenuMessage(uuid.toString(), CHANNEL_OPEN, id.toByteArray(StandardCharsets.UTF_8))
        }
    }

    fun sendInventoryStackCommand(uuid: UUID, msg: String){
        scope.launch {
            SurfGuiApi.getInstance().sendMenuMessage(uuid.toString(), CHANNEL_COMMANDS, msg.toByteArray(StandardCharsets.UTF_8))
        }
    }

    fun handleIncomingOpenRequest(uuid: String, menuID: String) = handleIncomingOpenRequest(UUID.fromString(uuid), menuID)

    fun handleIncomingOpenRequest(uuid: UUID, menuID: String){
        if (MenuService.isMenuRegistered(menuID.substringBefore(":"))){
            scope.launch {
                MenuService.openMenu(UserManager[uuid], menuID)
            }
        }else{
            SurfGuiApi.getInstance().log("Received request to open menu called '$menuID' could not be processed, because the Inventory has not been registered (yet?)", System.Logger.Level.ERROR)
            UserManager[uuid].sendMessage("<gradient:blue:#00aa00>|Surf -> Dieses Menu ist leider derzeit nicht verfügbar.".toComponent())//TODO Replace with actual SurfMessage
        }
    }


    fun bookToString(book: Book){
        val builder = StringBuilder()
        builder.append("").append("open_book:\uafa1").append(book.author());
        //TODO Complete implementation of a Book (de-)serializer to send
        // book pages from velocity to backend to open them from there

    }

    fun getChannelOpen() = CHANNEL_OPEN
    fun getChannelCommands() = CHANNEL_COMMANDS

}