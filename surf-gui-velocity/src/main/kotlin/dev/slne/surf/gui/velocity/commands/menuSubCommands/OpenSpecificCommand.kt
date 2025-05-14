package dev.slne.surf.gui.velocity.commands.menuSubCommands

import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.Player
import dev.slne.surf.gui.menu.menu.MenuService
import dev.slne.surf.gui.user.UserManager
import net.kyori.adventure.text.Component

class OpenSpecificCommand: SimpleCommand {
    //TODO Maybe implement that one into the Main Command
    override fun execute(p0: SimpleCommand.Invocation) {
        if (p0.source() !is Player){
            p0.source().sendMessage(Component.text("You do not have the permission to do that"))
            return
        }

        val player = p0.source() as Player
        MenuService.openMenu(UserManager[player.uniqueId], p0.arguments()[0])
    }

    override fun hasPermission(invocation: SimpleCommand.Invocation): Boolean {
        return true//TODO Fix this
    }

    override fun suggest(invocation: SimpleCommand.Invocation): List<String> {
        return MenuService.getAvailableMenus()
    }
}