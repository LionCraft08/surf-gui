package dev.slne.surf.gui.bukkit.commands.subcommands

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.gui.bukkit.SurfGuiBukkitApi
import dev.slne.surf.gui.bukkit.extensions.toUser
import dev.slne.surf.gui.plugin

val SurfGuiDebugCommand = subcommand("debug") {
//            withPermission("surf.gui.command.sgui.debug")

    subcommand("container-id") {
//            withPermission("surf.gui.command.sgui.debug.container-id")

        executesPlayer(PlayerCommandExecutor { player, args ->
            plugin.launch {
                val newId = SurfGuiBukkitApi.getNextContainerId(player.toUser())
                player.sendMessage("New container id: $newId")
            }
        })
    }
}