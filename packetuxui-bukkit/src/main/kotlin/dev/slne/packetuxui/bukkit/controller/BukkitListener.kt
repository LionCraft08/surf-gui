package dev.slne.packetuxui.bukkit.controller

import dev.slne.packetuxui.bukkit.extensions.toUser
import dev.slne.packetuxui.menu.menu.menuService
import dev.slne.packetuxui.user.UserManager
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