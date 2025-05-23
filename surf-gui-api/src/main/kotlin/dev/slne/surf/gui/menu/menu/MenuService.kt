package dev.slne.surf.gui.menu.menu

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow.WindowClickType
import dev.slne.surf.gui.SurfGuiApi
import dev.slne.surf.gui.common.*
import dev.slne.surf.gui.communication.CommunicationHandler
import dev.slne.surf.gui.dto.AccumulatedDrag
import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.button.ButtonType
import dev.slne.surf.gui.menu.button.buttons.TextInputConfirmButton
import dev.slne.surf.gui.menu.button.click.ClickData
import dev.slne.surf.gui.menu.button.click.ClickType
import dev.slne.surf.gui.menu.button.click.ExecuteComponent
import dev.slne.surf.gui.menu.button.click.TextExecuteComponent
import dev.slne.surf.gui.menu.menu.specific.TextInputMenu
import dev.slne.surf.gui.user.User
import dev.slne.surf.gui.util.DM
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import java.lang.RuntimeException
import java.util.*


val log = SurfGuiApi.getInstance()
val menuService = MenuService

/**
 * A service that manages menus.
 */
object MenuService {

    private val menus = mutableObject2ObjectMapOf<String, Class<out Menu>>()
    private val carriedItem = mutableObject2ObjectMapOf<User, ItemStack>().synchronize()
    private val accumulatedDrag =
        mutableObject2ObjectMapOf<User, ObjectList<AccumulatedDrag>>().synchronize()

    /**
     * Add a menu to the service.
     *
     * @param menu The menu to add.
     */
    fun registerNewMenu(id: String, clazz: Class<out Menu>) {
        if (id.contains(":")) log.log("The ID $id contains ':' wich is not allowed. The Registered Menu probably won't be openable by the System", System.Logger.Level.ERROR)
        menus.put(id, clazz)
    }

    /**
     * Remove a menu from the service.
     * @param id The menu to remove.
     */
    fun unregisterMenu(id: String) = menus.remove(id)

    /**Gets the Class of a registered Menu
     * @param id The ID of the Menu
     * @return The CLass of a registered Menu if available
     */
    fun getMenu(id: String): Class<out Menu>? = menus.get(id)

    fun isMenuRegistered(menuID: String):Boolean{
        return if(menuID == "book") true else menus.contains(menuID)
    }

    fun openMenu(user: User, menuArgs: String){
        //Splitting the id into id and parameter
        var menuID = menuArgs
        var args = ""
        if (menuID.contains(":")){
            args = menuID.substringAfter(":")
            menuID = menuID.substringBefore(":")
        }

        user.getActiveMenu()?.close(user)


        if (menuID.startsWith("book:")){
            BookProvider.open(menuID.substringAfter("book:"), user)
            return
        }



        val menuClass = getMenu(menuID)
        if (menuClass == null) {
            CommunicationHandler.sendInventoryRequest(user.uuid, menuArgs)
            return
        }
        var menu: Menu? = null

        //Attempt to open Menu with args
        if (args.isNotBlank()){
            try {
                menu = menuClass.getConstructor(String::class.java).newInstance(args)
                menu.open(user)
            }catch (e: NoSuchMethodException){
                log.log("User ${user.uuid} tried to open $menuID with args $args but this Menu doesn't support args.",
                    System.Logger.Level.WARNING )
            }
        }
        //Attempt to open Menu with User parameter
        if (menu == null) try {
            menu = menuClass.getConstructor(User::class.java).newInstance(user)
            menu.open(user)
        }catch (_: NoSuchMethodException){ }

        //Attempt to open Menu without args
         if (menu == null) try {
             menu = menuClass.getConstructor().newInstance()
             menu.open(user)
         }catch (e: NoSuchMethodException){
             user.sendMessage(DM.fatalError("Dieses Menu ist leider derzeit nicht verfügbar. Sollte dieser Fehler weiterhin bestehen, melde ihn bitte über Discord."))
             throw RuntimeException("MenuClass $menuID (${menuClass.name}) is Invalid because it has either not been registered or does not have a Constructor without parameters.")
         }
        if (menu.registerInStackTrace) user.addOpenedMenu(menuArgs)
    }

    fun openMenu(user: User, menuID: String, args: String){
        openMenu(user, "$menuID:$args")
    }

    fun getAvailableMenus(): List<String> {
        return menus.keys.toList()
    }

    /**
     * Close a menu for a user.
     * Internal use, u should use [Menu.close] instead
     *
     * @param user The user to close the menu for.
     */
    fun onCloseMenu(user: User) {
        carriedItem.remove(user)
        clearAccumulatedDrag(user)
    }

    /**
     * Handle a click in the inventory.
     *
     * @param user The user that clicked.
     * @param packet The click packet.
     */
    fun handleClickInventory(user: User, packet: WrapperPlayClientClickWindow) {
        val menu = user.getActiveMenu() ?: error("Menu not found for container id.")
        val clickData = getClickType(packet)

        updateCarriedItem(user, packet.carriedItemStack, clickData.clickType)

        if (clickData.clickType == ClickType.DRAG_END) {
            handleDragEnd(user, menu)
        }

        user.receivePacket(createAdjustedClickPacket(packet, menu))
    }

    /**
     * Handle a click in a menu.
     *
     * @param user The user that clicked.
     * @param clickData The click data.
     * @param slot The slot that was clicked.
     */
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
        user.updateInventory()

        val executeComponent = ExecuteComponent(user, clickData.buttonType, slot, carriedItem, menu)

        if (!cooldown.isFreezeExpired(now)) {
            return
        } else if (!cooldown.isTimeExpired(now)) {
            button.cooldown.resetFreeze()
            button.cooldown.execute?.invoke(executeComponent)
            return
        } else {
            button.cooldown.resetExpire()
        }
        if (button is TextInputConfirmButton)
            button.textExecute.invoke(TextExecuteComponent(user, clickData.buttonType, slot, carriedItem, menu,
                (menu as TextInputMenu).text))
        else button.onClick(executeComponent)
    }

    /**
     * Update a button in a menu.
     *
     * @param user The user that clicked.
     * @param newButton The new button to set.
     * @param slot The slot to update.
     */
    fun updateButton(user: User, newButton: Button, slot: Int) {
        val menu = user.getActiveMenu() ?: return
        require(slot in 0..menu.type.lastIndex) { "Slot out of range." }

        menu.updateButton(user, slot, newButton)
    }

    /**
     * Update multiple buttons in a menu.
     *
     * @param user The user that clicked.
     * @param newButtons The new buttons to set.
     */
    fun updateButtons(user: User, newButtons: Int2ObjectMap<Button>) {
        val menu = user.getActiveMenu() ?: return
        require(newButtons.keys.any { it in 0..menu.type.lastIndex }) { "Slot out of range." }

        menu.buttons.clear()
        menu.buttons.putAll(newButtons)

        menu.sendWindowItems(user, null)
        menu.updateSlots(user)
    }

    /**
     * Check if a click is a menu click.
     *
     * @param wrapper The click packet.
     * @param clickType The click type.
     * @param user The user that clicked.
     */
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

    /**
     * Get the click type from a click packet.
     *
     * @param packet The click packet.
     */
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

    /**
     * Accumulate a drag click.
     *
     * @param user The user that clicked.
     * @param packet The click packet.
     * @param type The click type.
     */
    fun accumulateDrag(user: User, packet: WrapperPlayClientClickWindow, type: ClickType) {
        accumulatedDrag.computeIfAbsent(user, Object2ObjectFunction {
            mutableObjectListOf()
        }).add(AccumulatedDrag(packet, type))
    }

    /**
     * Handle the end of a drag.
     *
     * @param user The user that clicked.
     * @param menu The menu that was clicked.
     */
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

    /**
     * Create a drag packet.
     *
     * @param originalPacket The original click packet.
     * @param slotOffset The slot offset.
     *
     * @return The new click packet.
     */
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

    /**
     * Clear the accumulated drag clicks.
     *
     * @param user The user to clear the drag clicks for.
     */
    private fun clearAccumulatedDrag(user: User) {
        accumulatedDrag[user]?.clear()
    }

    /**
     * Create an adjusted click packet.
     *
     * @param packet The original click packet.
     * @param menu The menu that was clicked.
     *
     * @return The new click packet.
     */
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

    /**
     * Update the carried item for a user.
     *
     * @param user The user to update the carried item for.
     * @param carriedItemStack The carried item stack.
     * @param clickType The click type.
     */
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

