package net.craftoriya.packetuxui.bukkit.commands.subcommands

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import net.craftoriya.packetuxui.bukkit.PacketUxUiBukkitApi
import net.craftoriya.packetuxui.bukkit.extensions.toUser
import net.craftoriya.packetuxui.plugin

val PacketUxUiDebugCommand = subcommand("debug") {
//    withPermission("packetuxui.command.packetuxui.debug")

    subcommand("container-id") {
//        withPermission("packetuxui.command.packetuxui.debug.container-id")

        executesPlayer(PlayerCommandExecutor { player, args ->
            plugin.launch {
                val newId = PacketUxUiBukkitApi.getNextContainerId(player.toUser())
                player.sendMessage("New container id: $newId")
            }
        })
    }
}