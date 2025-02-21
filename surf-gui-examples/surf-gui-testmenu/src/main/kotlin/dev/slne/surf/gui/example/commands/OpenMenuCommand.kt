package dev.slne.surf.gui.example.commands

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.gui.bukkit.extensions.toUser
import dev.slne.surf.gui.example.menus.AllInOne
import dev.slne.surf.gui.example.menus.PaginatedMenuTest
import dev.slne.surf.gui.example.plugin

object OpenMenuCommand {

    init {
        commandAPICommand("openmenu") {
            stringArgument("menu") {
                replaceSuggestions(
                    ArgumentSuggestions.strings(
                        "all_in_one"
                    )
                )
            }

            executesPlayer(PlayerCommandExecutor { player, args ->
                val menuName = args["menu"] as String
                val menuClass = when (menuName) {
                    "all_in_one" -> AllInOne()
                    "paginated" -> PaginatedMenuTest()
                    else -> null
                }

                val menu = when (menuClass) {
                    is AllInOne -> menuClass.menu
                    is PaginatedMenuTest -> menuClass
                    else -> null
                }

                if (menu == null) {
                    player.sendMessage("Menu not found")
                    return@PlayerCommandExecutor
                }

                plugin.launch {
                    menu.open(player.toUser())
                }

                if (menuClass is AllInOne) {
                    menuClass.startUpdate()
                }
            })
        }
    }
}