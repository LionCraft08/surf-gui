package dev.slne.surf.gui.velocity.commands

import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.Player
import dev.slne.surf.gui.menu.menu.MenuService
import dev.slne.surf.gui.user.UserManager

class MenuCommand : SimpleCommand{
    override fun execute(invocation: SimpleCommand.Invocation) {
        val player = if(invocation.source() is Player) invocation.source() as Player else return
        MenuService.openMenu(UserManager[player.uniqueId], "main_menu")
    }
}