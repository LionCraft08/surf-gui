package dev.slne.surf.gui.bukkit.extensions

import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.menu.MenuService
import org.bukkit.entity.Player

fun MenuService.updateButton(player: Player, button: Button, slot: Int) =
    updateButton(player.toUser(), button, slot)