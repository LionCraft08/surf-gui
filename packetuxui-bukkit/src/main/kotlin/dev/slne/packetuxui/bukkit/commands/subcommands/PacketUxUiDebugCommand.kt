package dev.slne.packetuxui.bukkit.commands.subcommands

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.packetuxui.bukkit.PacketUxUiBukkitApi
import dev.slne.packetuxui.bukkit.extensions.toUser
import dev.slne.packetuxui.plugin

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