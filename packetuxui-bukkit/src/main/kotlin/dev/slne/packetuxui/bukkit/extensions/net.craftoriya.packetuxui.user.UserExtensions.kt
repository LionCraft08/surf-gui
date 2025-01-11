package dev.slne.packetuxui.bukkit.extensions

import dev.slne.packetuxui.user.User
import dev.slne.packetuxui.user.UserManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun Player.toUser() = UserManager[this.uniqueId]
fun User.toPlayer() = Bukkit.getPlayer(this.uuid)
fun User.toOfflinePlayer() = Bukkit.getOfflinePlayer(this.uuid)