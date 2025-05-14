package dev.slne.surf.gui.listeners

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindowButton
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientNameItem
import dev.slne.surf.gui.menu.button.click.ClickType
import dev.slne.surf.gui.menu.menu.menuService
import dev.slne.surf.gui.menu.menu.specific.TextInputMenu
import dev.slne.surf.gui.user.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PacketListener : PacketListenerAbstract(PacketListenerPriority.HIGHEST) {

    private val packetScope = CoroutineScope(Dispatchers.Default)

    override fun onPacketReceive(event: PacketReceiveEvent) {
        val packetType = event.packetType
        val packetUser = event.user
        val packetUserUuid = packetUser.uuid ?: return
        val user = UserManager[packetUserUuid]
        val menu = user.getActiveMenu()

        // Handle close window packet first
        if (packetType == PacketType.Play.Client.CLOSE_WINDOW) {
            packetScope.launch {
                menu?.close(user)
            }

            menuService.onCloseMenu(user)
            return
        }

        if (packetType == PacketType.Play.Client.NAME_ITEM){
            val packet = WrapperPlayClientNameItem(event)
            if (menu is TextInputMenu)
                menu.text = packet.itemName
            return
        }
        // Check if packet is click window, if not exit
        if (packetType != PacketType.Play.Client.CLICK_WINDOW) {
            return
        }

        val packet = WrapperPlayClientClickWindow(event)

        // Check if the clicked window is an actual menu, if not exit
        if (menu == null) {
            return
        }

        event.isCancelled = true

        val clickData = menuService.getClickType(packet)

        // If drag, handle drag
        if (clickData.clickType == ClickType.DRAG_START || clickData.clickType == ClickType.DRAG_ADD) {
            menuService.accumulateDrag(user, packet, clickData.clickType)
            return
        }

        val menuClickData = menuService.isMenuClick(packet, clickData.clickType, user)

        // Check if click is a menu click or inventory click
        if (menuClickData) {
            menuService.handleClickMenu(user, clickData, packet.slot)
            //user.updateInventory() Just why?
        } else { // isInventoryClick
            menuService.handleClickInventory(user, packet)
        }
    }
}