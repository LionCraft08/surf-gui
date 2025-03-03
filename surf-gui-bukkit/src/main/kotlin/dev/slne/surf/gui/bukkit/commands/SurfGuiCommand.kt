package dev.slne.surf.gui.bukkit.commands

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.gui.bukkit.commands.subcommands.SurfGuiDebugCommand

object SurfGuiCommand {

    init {
        println("Should register")
        commandAPICommand("surfgui") {
            println("Registering")
            withAliases("sgui")
//            withPermission("surf.gui.command.sgui")

            withSubcommands(SurfGuiDebugCommand)
        }
    }

}