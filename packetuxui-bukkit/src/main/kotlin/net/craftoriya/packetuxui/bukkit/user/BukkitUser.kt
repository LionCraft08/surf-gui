package net.craftoriya.packetuxui.bukkit.user

import com.github.retrooper.packetevents.PacketEvents
import net.craftoriya.packetuxui.bukkit.extensions.toPlayer
import net.craftoriya.packetuxui.user.AbstractUser
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.*
import com.github.retrooper.packetevents.protocol.player.User as PacketUser

class BukkitUser(override val uuid: UUID) : AbstractUser(uuid) {

    override fun updateInventory() {
        player?.updateInventory()
    }

    override val packetUser: PacketUser?
        get() = player?.let { PacketEvents.getAPI().playerManager.getUser(it) }

    override val player: Player?
        get() = toPlayer()

    override fun sendMessage(message: Component) {
        player?.sendMessage(message)
    }

}