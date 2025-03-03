package dev.slne.surf.gui.bukkit.extensions

import dev.slne.surf.gui.user.User
import dev.slne.surf.gui.user.UserManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun Player.toUser() = UserManager[this.uniqueId]
fun User.toPlayer() = Bukkit.getPlayer(this.uuid)
fun User.toOfflinePlayer() = Bukkit.getOfflinePlayer(this.uuid)