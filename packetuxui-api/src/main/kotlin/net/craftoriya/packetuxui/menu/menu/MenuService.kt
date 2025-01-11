package net.craftoriya.packetuxui.menu.menu

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow.WindowClickType
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectList
import net.craftoriya.packetuxui.common.*
import net.craftoriya.packetuxui.dto.AccumulatedDrag
import net.craftoriya.packetuxui.menu.button.Button
import net.craftoriya.packetuxui.menu.button.ButtonType
import net.craftoriya.packetuxui.menu.button.click.ClickData
import net.craftoriya.packetuxui.menu.button.click.ClickType
import net.craftoriya.packetuxui.menu.button.click.ExecuteComponent
import net.craftoriya.packetuxui.user.User
import java.util.*

val menuService = MenuService

object MenuService {

    private val _menus = mutableObjectSetOf<Menu>()
    private val carriedItem = mutableObject2ObjectMapOf<User, ItemStack>().synchronize()
    private val accumulatedDrag =
        mutableObject2ObjectMapOf<User, ObjectList<AccumulatedDrag>>().synchronize()

    val menus = _menus.freeze()

    fun addMenu(menu: Menu) {
        _menus.add(menu)
    }

    fun removeMenu(menu: Menu) {
        _menus.remove(menu)
    }

    fun onCloseMenu(user: User) {
        carriedItem.remove(user)
        clearAccumulatedDrag(user)
    }

    fun handleClickInventory(user: User, packet: WrapperPlayClientClickWindow) {
        val menu = user.getActiveMenu() ?: error("Menu not found for container id.")
        val clickData = getClickType(packet)

        updateCarriedItem(user, packet.carriedItemStack, clickData.clickType)

        if (clickData.clickType == ClickType.DRAG_END) {
            handleDragEnd(user, menu)
        }

        user.receivePacket(createAdjustedClickPacket(packet, menu))
    }

    fun handleClickMenu(user: User, clickData: ClickData, slot: Int) {
        if (clickData.clickType == ClickType.DRAG_END) {
            clearAccumulatedDrag(user)
        }

        val carriedItem = carriedItem[user]
        val menu = user.getActiveMenu() ?: error("Menu under user key not found.")

        val button = menu.buttons[slot]
        if (button == null) {
            menu.sendWindowItems(user, carriedItem)
            return
        }

        val now = System.currentTimeMillis()
        val cooldown = button.cooldown.combine(menu.cooldown)

        menu.sendWindowItems(user, carriedItem)

        val executeComponent = ExecuteComponent(user, clickData.buttonType, slot, carriedItem, menu)

        if (!cooldown.isFreezeExpired(now)) {
            return
        } else if (!cooldown.isTimeExpired(now)) {
            button.cooldown.resetFreeze()
            button.cooldown.execute?.invoke(executeComponent)
            return
        } else {
            button.cooldown.resetTime()
        }

        button.onClick(executeComponent)
    }

    fun updateButton(user: User, newButton: Button, slot: Int) {
        val menu = user.getActiveMenu() ?: return
        require(slot in 0..menu.type.lastIndex) { "Slot out of range." }

        menu.updateButton(user, slot, newButton)
    }

    fun updateButtons(user: User, newButtons: Int2ObjectMap<Button>) {
        val menu = user.getActiveMenu() ?: return
        require(newButtons.keys.any { it in 0..menu.type.lastIndex }) { "Slot out of range." }

        menu.buttons.clear()
        menu.buttons.putAll(newButtons)

        menu.sendWindowItems(user, null)
        menu.updateSlots(user)
    }

    fun isMenuClick(
        wrapper: WrapperPlayClientClickWindow,
        clickType: ClickType,
        user: User
    ): Boolean {
        val menu = user.getActiveMenu() ?: return false
        val slotRange = 0..menu.type.lastIndex

        return when (clickType) {
            ClickType.SHIFT_CLICK -> true
            ClickType.PICKUP, ClickType.PLACE -> wrapper.slot in slotRange
            ClickType.DRAG_END, ClickType.PICKUP_ALL ->
                wrapper.slot in slotRange || wrapper.slots.map { it.keys.any { it in slotRange } }
                    .orElse(false)

            else -> false
        }
    }

    fun getClickType(packet: WrapperPlayClientClickWindow): ClickData {
        return when (packet.windowClickType) {
            WindowClickType.PICKUP -> {
                val carriedItem = packet.carriedItemStack
                val isCarriedItemExist =
                    carriedItem != null && carriedItem != ItemStack.EMPTY && carriedItem.type != ItemTypes.AIR
                when (packet.button) {
                    0 -> ClickData(
                        ButtonType.LEFT,
                        if (isCarriedItemExist) ClickType.PICKUP else ClickType.PLACE
                    )

                    else -> ClickData(
                        ButtonType.RIGHT,
                        if (isCarriedItemExist) ClickType.PLACE else ClickType.PICKUP
                    )
                }
            }

            WindowClickType.QUICK_MOVE -> {
                if (packet.button == 0) {
                    ClickData(ButtonType.SHIFT_LEFT, ClickType.SHIFT_CLICK)
                } else {
                    ClickData(ButtonType.SHIFT_RIGHT, ClickType.SHIFT_CLICK)
                }
            }

            WindowClickType.SWAP -> {
                when (packet.button) {
                    in 0..8 -> ClickData(ButtonType.entries[9 + packet.button], ClickType.PICKUP)
                    40 -> ClickData(ButtonType.SWAP_ITEM, ClickType.PICKUP)
                    else -> ClickData(ButtonType.LEFT, ClickType.PLACE)
                }
            }

            WindowClickType.CLONE -> {
                ClickData(ButtonType.MIDDLE, ClickType.PICKUP)
            }

            WindowClickType.THROW -> {
                if (packet.button == 0) {
                    ClickData(ButtonType.DROP, ClickType.PICKUP)
                } else {
                    ClickData(ButtonType.CTRL_DROP, ClickType.PICKUP)
                }
            }

            WindowClickType.QUICK_CRAFT -> {
                when (packet.button) {
                    0 -> ClickData(ButtonType.LEFT, ClickType.DRAG_START)
                    4 -> ClickData(ButtonType.RIGHT, ClickType.DRAG_START)
                    8 -> ClickData(ButtonType.MIDDLE, ClickType.DRAG_START)

                    1 -> ClickData(ButtonType.LEFT, ClickType.DRAG_ADD)
                    5 -> ClickData(ButtonType.RIGHT, ClickType.DRAG_ADD)
                    9 -> ClickData(ButtonType.MIDDLE, ClickType.DRAG_ADD)

                    2 -> ClickData(ButtonType.LEFT, ClickType.DRAG_END)
                    6 -> ClickData(ButtonType.RIGHT, ClickType.DRAG_END)
                    10 -> ClickData(ButtonType.MIDDLE, ClickType.DRAG_END)

                    else -> ClickData(ButtonType.LEFT, ClickType.UNDEFINED)
                }
            }

            WindowClickType.PICKUP_ALL -> {
                ClickData(ButtonType.DOUBLE_CLICK, ClickType.PICKUP_ALL)
            }

            else -> {
                ClickData(ButtonType.LEFT, ClickType.UNDEFINED)
            }
        }
    }

    fun accumulateDrag(user: User, packet: WrapperPlayClientClickWindow, type: ClickType) {
        accumulatedDrag.computeIfAbsent(user) { mutableObjectListOf() }
            .add(AccumulatedDrag(packet, type))
    }

    private fun handleDragEnd(user: User, menu: Menu) {
        accumulatedDrag[user]?.forEach { drag ->
            val packet = if (drag.type == ClickType.DRAG_START) {
                createDragPacket(drag.packet, 0)
            } else {
                createDragPacket(drag.packet, -menu.type.size + 9)
            }
            user.receivePacket(packet)
        }
        clearAccumulatedDrag(user)
    }

    private fun createDragPacket(
        originalPacket: WrapperPlayClientClickWindow,
        slotOffset: Int
    ): WrapperPlayClientClickWindow {
        return WrapperPlayClientClickWindow(
            originalPacket.windowId,
            originalPacket.stateId,
            originalPacket.slot + slotOffset,
            originalPacket.button,
            originalPacket.actionNumber,
            originalPacket.windowClickType,
            Optional.of(mutableMapOf()),
            originalPacket.carriedItemStack
        )
    }

    private fun clearAccumulatedDrag(user: User) {
        accumulatedDrag[user]?.clear()
    }

    private fun createAdjustedClickPacket(
        packet: WrapperPlayClientClickWindow,
        menu: Menu
    ): WrapperPlayClientClickWindow {
        val slotOffset = if (packet.slot != -999) packet.slot - menu.type.size + 9 else -999
        val adjustedSlots = packet.slots.orElse(emptyMap()).mapKeys { (slot, _) ->
            slot - menu.type.size + 9
        }

        return WrapperPlayClientClickWindow(
            packet.windowId, packet.stateId, slotOffset, packet.button,
            packet.actionNumber, packet.windowClickType,
            Optional.of(adjustedSlots), packet.carriedItemStack
        )
    }

    private fun updateCarriedItem(
        user: User,
        carriedItemStack: ItemStack?,
        clickType: ClickType
    ) {
        if (carriedItemStack == null || carriedItemStack.type == ItemTypes.AIR) {
            carriedItem.remove(user)
            return
        }

        when (clickType) {
            ClickType.PICKUP, ClickType.PICKUP_ALL, ClickType.DRAG_START, ClickType.DRAG_END -> {
                carriedItem[user] = carriedItemStack
            }

            else -> carriedItem.remove(user)
        }
    }
}

