package net.craftoriya.packetuxui.listeners

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.craftoriya.packetuxui.menu.button.click.ClickType
import net.craftoriya.packetuxui.menu.menu.findMatchingMenu
import net.craftoriya.packetuxui.menu.menu.menuService
import net.craftoriya.packetuxui.user.UserManager

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
            packetScope.launch { // TODO: Check me twisti
                menu?.close(user)
            }

            menuService.onCloseMenu(user)
            return
        }

        // Check if packet is click window, if not exit
        if (packetType != PacketType.Play.Client.CLICK_WINDOW) {
            return
        }

        val packet = WrapperPlayClientClickWindow(event)

        // Check if clicked window is an actual menu, if not exit
        if (findMatchingMenu(user, packet.windowId) == null) {
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
            user.updateInventory()
        } else { // isInventoryClick
            menuService.handleClickInventory(user, packet)
        }
    }
}