package dev.slne.packetuxui.bukkit.extensions

import dev.slne.packetuxui.menu.button.Button
import dev.slne.packetuxui.menu.menu.MenuService
import org.bukkit.entity.Player

fun MenuService.updateButton(player: Player, button: Button, slot: Int) =
    updateButton(player.toUser(), button, slot)