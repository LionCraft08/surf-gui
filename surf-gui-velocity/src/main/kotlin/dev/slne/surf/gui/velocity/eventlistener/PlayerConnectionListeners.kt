package dev.slne.surf.gui.velocity.eventlistener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import dev.slne.surf.gui.user.UserManager

class PlayerConnectionListeners {
    @Subscribe
    fun onDisconnect(e: DisconnectEvent){
        UserManager[e.player.uniqueId].closeCurrentMenu()
        UserManager.remove(e.player.uniqueId)
    }
}