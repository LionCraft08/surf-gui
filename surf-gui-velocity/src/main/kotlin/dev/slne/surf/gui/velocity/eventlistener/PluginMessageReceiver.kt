package dev.slne.surf.gui.velocity.eventlistener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import dev.slne.surf.gui.SurfGuiApi
import dev.slne.surf.gui.communication.CommunicationHandler
import dev.slne.surf.gui.user.UserManager

class PluginMessageReceiver {
    companion object{
        val menu_open_requests = MinecraftChannelIdentifier.from(CommunicationHandler.getChannelOpen())
        val menu_commands = MinecraftChannelIdentifier.from(CommunicationHandler.getChannelCommands())

        public fun getMenuOpenRequests() = menu_open_requests
        public fun getMenuCommands() = menu_commands
    }

    @Subscribe
    fun onPM(e: PluginMessageEvent){
        if (e.identifier != menu_open_requests&&e.identifier!=menu_commands) return
        if (e.data == null) {
            SurfGuiApi.getInstance().debug(
                "Received Plugin Message from channel ${e.identifier} for Player ${e.source} containing no data! Cancelling handle")
            return
        }
        SurfGuiApi.getInstance().debug("Received Plugin Message from channel ${e.identifier} for Player ${e.source} containing")
        if (e.source !is ServerConnection){
            SurfGuiApi.getInstance().log("Cancelling a request sent from the Client of ${e.source} to protect the Network!")
            return
        }
        val connection = e.source as ServerConnection

        //If Command -> handle as Cmd; If open -> Open the Menu
        if (e.identifier == menu_commands) handleCommand(e, connection.player)
        else CommunicationHandler.handleIncomingOpenRequest(connection.player.uniqueId, String(e.data))

    }

    private fun handleCommand(e: PluginMessageEvent, player:Player){
        val content = String(e.data)
        when {
            content.startsWith("open_previous") -> {
                UserManager[player.uniqueId].openPreviousMenu()
            }
            content.startsWith("open")->{
                UserManager[player.uniqueId].addOpenedMenu(content.substringAfter(":"))
            }
            else->{
                SurfGuiApi.getInstance().log("Invalid Command used: $content", System.Logger.Level.WARNING)
            }
        }
    }
}