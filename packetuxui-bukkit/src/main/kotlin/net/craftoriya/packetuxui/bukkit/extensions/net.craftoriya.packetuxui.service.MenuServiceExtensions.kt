package net.craftoriya.packetuxui.bukkit.extensions

import net.craftoriya.packetuxui.service.Button
import net.craftoriya.packetuxui.service.MenuService
import org.bukkit.entity.Player

fun MenuService.updateButton(player: Player, button: Button, slot: Int) =
    updateButton(player.toUser(), button, slot)