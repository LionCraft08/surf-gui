package dev.slne.surf.gui.bukkit.controller

import dev.slne.surf.gui.bukkit.extensions.toUser
import dev.slne.surf.gui.communication.CommunicationHandler
import net.kyori.adventure.inventory.Book
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.lang.NullPointerException
import java.lang.RuntimeException

class PluginMessageReceiver : PluginMessageListener{
    override fun onPluginMessageReceived(
        channel: String,
        player: Player,
        message: ByteArray?
    ) {
        if (channel == CommunicationHandler.getChannelOpen()){
            if (message == null) throw RuntimeException(NullPointerException("Received Plugin message from $channel for ${player.name} without message"))
            CommunicationHandler.handleIncomingOpenRequest(player.uniqueId.toString(), String(message))
        }
        if (channel == CommunicationHandler.getChannelCommands()){
            if (message == null) throw RuntimeException(NullPointerException("Received Plugin message from $channel for ${player.name} without message"))
        }
    }
}