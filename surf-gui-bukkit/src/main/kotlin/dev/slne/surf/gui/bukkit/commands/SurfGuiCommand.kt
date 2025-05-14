package dev.slne.surf.gui.bukkit.commands

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.gui.bukkit.extensions.toUser
import dev.slne.surf.gui.menu.menu.MenuService

object SurfGuiCommand {

    init {
        println("Should register")
        commandAPICommand("surfgui") {
            println("Registering")
            stringArgument("gui", true)
            withAliases("sgui")
            playerExecutor { player, arguments ->
                MenuService.openMenu(player.toUser(), (arguments.getRaw(0)?:"main_menu"))
            }
//            withPermission("surf.gui.command.sgui")

        }
    }

}