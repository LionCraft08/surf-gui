package net.craftoriya.packetuxui.user

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import net.craftoriya.packetuxui.menu.menu.Menu
import net.kyori.adventure.text.Component
import java.util.*
import com.github.retrooper.packetevents.protocol.player.User as PacketUser

interface User { // TODO: execute command method (removed from Button builder)

    val uuid: UUID
    val packetUser: PacketUser?
    val player: Any?

    fun getActiveMenu(): Menu?

    fun updateInventory()

    fun sendPacket(wrapper: PacketWrapper<*>) = this.packetUser?.sendPacket(wrapper)

    fun receivePacket(wrapper: PacketWrapper<*>) =
        PacketEvents.getAPI().playerManager.receivePacketSilently(player, wrapper)

    fun sendMessage(message: Component)
}