package dev.slne.surf.gui.bukkit.controller

import dev.slne.surf.gui.bukkit.extensions.toUser
import dev.slne.surf.gui.menu.menu.menuService
import dev.slne.surf.gui.user.UserManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object BukkitListener : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        menuService.onCloseMenu(event.player.toUser())
        UserManager.remove(event.player.uniqueId)
    }
}