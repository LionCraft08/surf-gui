package net.craftoriya.packetuxui.menu.menu

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import kotlinx.coroutines.*
import net.craftoriya.packetuxui.api
import net.craftoriya.packetuxui.common.*
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.menu.button.Button
import net.craftoriya.packetuxui.menu.button.ButtonBuilder
import net.craftoriya.packetuxui.menu.button.ButtonBuilderDslMarker
import net.craftoriya.packetuxui.menu.button.click.ExecutableComponent
import net.craftoriya.packetuxui.menu.button.click.ExecutableComponentMarker
import net.craftoriya.packetuxui.menu.utils.Slot
import net.craftoriya.packetuxui.menu.utils.SlotRange
import net.craftoriya.packetuxui.user.AbstractUser
import net.craftoriya.packetuxui.user.User
import net.kyori.adventure.text.Component

typealias MenuJob = suspend CoroutineScope.() -> Unit

fun findMatchingMenu(user: User, containerId: Int) =
    menuService.menus.find { it.getContainerId(user) == containerId }

open class Menu(
    val name: Component,
    val type: MenuType,
    buttons: Int2ObjectMap<Button>,
    val cooldown: CooldownComponent = CooldownComponent.EMPTY, // TODO: Check this?
    private val coroutineScope: CoroutineScope = CoroutineScope(
        Dispatchers.Default + CoroutineName(
            "Menu-${name.toPlain()}"
        )
    )
) {
    val buttons = mutableInt2ObjectMapOf(buttons).synchronize()
    val viewers = mutableObject2IntMapOf<User>().synchronize()

    private var menuScope = CoroutineScope(coroutineScope.coroutineContext + SupervisorJob())
    private val blocks = mutableObjectListOf<MenuJob>()
    private var shouldRelaunch = false

    init {
        check(buttons.size <= type.size) { "Too many items in menu" }
    }

    fun launchJob(block: MenuJob) {
        menuScope.launch { block() }
        blocks.add(block)
    }

    suspend fun open(user: User) {
        if (shouldRelaunch) {
            shouldRelaunch = false
            menuScope = CoroutineScope(coroutineScope.coroutineContext + SupervisorJob())

            for (block in blocks) {
                menuScope.launch { block() }
            }
        }

        val containerId = api.getNextContainerId(user)

        viewers.put(user, containerId)
        (user as AbstractUser).setActiveMenu(this)

        user.sendPacket(WrapperPlayServerOpenWindow(containerId, type.id(), name))
        sendWindowItems(user)

        menuService.addMenu(this)
    }

    fun updateItem(user: User, slot: Int, item: ItemStack) {
        buttons[slot].item = item

        sendWindowItems(user)
    }

    fun updateButton(user: User, slot: Int, button: Button) {
        buttons[slot] = button

        sendWindowItems(user)
    }

    fun sendWindowItems(user: User, carriedItem: ItemStack? = null) {
        val containerId = getContainerId(user) ?: return

        val items = MutableList(type.size) { index ->
            this.buttons[index]?.item ?: ItemStack.EMPTY
        }

        user.sendPacket(WrapperPlayServerWindowItems(containerId, 0, items, carriedItem))
    }

    fun updateSlots(user: User) {
        val containerId = getContainerId(user) ?: return

        for ((slot, button) in buttons) {
            user.sendPacket(WrapperPlayServerSetSlot(containerId, 0, slot, button.item))
        }
    }

    suspend fun close(user: User) {
        viewers.removeInt(user)

        val hasOpenedContainer = api.hasOpenedContainer(user)

        if (!hasOpenedContainer) {
            (user as AbstractUser).setActiveMenu(null)
        }

        if (viewers.isEmpty()) {
            destroy()
        }
    }

    private fun destroy() {
        menuScope.cancel()
        shouldRelaunch = true

        println("Destroying menu ${name.toPlain()}")

        menuService.removeMenu(this)
    }

    fun getContainerId(user: User): Int? {
        return viewers.getInt(user)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Menu

        return name == other.name && type == other.type
    }

    override fun hashCode(): Int {
        return name.hashCode() + type.hashCode()
    }

    override fun toString(): String {
        return "Menu(name=${name.toPlain()}, type=$type, buttons=${buttons.size}, viewers=$viewers)"
    }

}

/**
 * DSL annotation for menu building blocks.
 */
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@DslMarker
annotation class MenuBuilderDslMarker

/**
 * DSL to create a simple menu.
 *
 * @param type The [MenuType], defining the size and layout of the menu.
 * @param builder A lambda to configure the menu.
 * @return A fully configured [Menu].
 */
inline fun menu(
    type: MenuType,
    builder: @MenuBuilderDslMarker MenuBuilderDsl.() -> Unit
): Menu {
    return MenuBuilderDsl(type).apply(builder).build()
}

/**
 * Builder class for creating a menu using a DSL.
 *
 * @property type The [MenuType] defining the size and layout of the menu.
 */
@MenuBuilderDslMarker
open class MenuBuilderDsl(val type: MenuType) {
    var name: Component = Component.empty()
    private val buttons = mutableInt2ObjectMapOf<Button>(type.size)
    private var cooldown = CooldownComponent.EMPTY

    /**
     * Add a button at a specific slot.
     *
     * @param slot The slot index where the button will be placed.
     * @param button The button to add.
     */
    fun button(slot: Int, button: Button) {
        buttons[slot] = button
    }

    /**
     * Add a button at a specific [Slot].
     *
     * @param slot The slot where the button will be placed.
     * @param button The button to add.
     */
    fun button(slot: Slot, button: Button) = button(slot.toSlot(), button)

    /**
     * Add a button using a builder lambda.
     *
     * @param slot The slot index where the button will be placed.
     * @param builder The builder lambda for the button.
     */
    fun button(slot: Int, builder: @ButtonBuilderDslMarker ButtonBuilder.() -> Unit) =
        button(slot, Button(builder))

    /**
     * Add a button at a specific [Slot] using a builder lambda.
     *
     * @param slot The slot where the button will be placed.
     * @param builder The builder lambda for the button.
     */
    fun button(slot: Slot, builder: @ButtonBuilderDslMarker ButtonBuilder.() -> Unit) =
        button(slot.toSlot(), builder)

    /**
     * Add multiple buttons for a given slot range.
     *
     * @param range The [SlotRange] where buttons will be placed.
     * @param builder The builder lambda for each slot in the range.
     */
    fun buttons(range: SlotRange, builder: @ButtonBuilderDslMarker ButtonBuilder.(Slot) -> Unit) {
        require(range.compatibleWith(type)) { "Slot range is not compatible with menu type" }

        for (slot in range) {
            buttons[slot.toSlot()] = ButtonBuilder().apply { builder(slot) }.build()
        }
    }

    /**
     * Add buttons to all available slots.
     *
     * @param builder The builder lambda for each slot.
     */
    fun buildAllButtons(builder: @ButtonBuilderDslMarker ButtonBuilder.(Slot) -> Unit) {
        buttons(SlotRange(Slot(0), Slot(type.size - 1)), builder)
    }

    /**
     * Set a cooldown for the menu.
     *
     * @param cooldown The [CooldownComponent] to use for the menu.
     */
    fun menuCooldown(cooldown: CooldownComponent) {
        this.cooldown = cooldown
    }

    /**
     * Set a cooldown with individual parameters.
     *
     * @param delay The delay before the cooldown starts.
     * @param freeze The freeze duration during cooldown.
     * @param execute The optional [ExecutableComponentMarker] to execute.
     */
    fun menuCooldown(
        delay: Long = 0,
        freeze: Long = 0,
        execute: @ExecutableComponentMarker ExecutableComponent? = null
    ) {
        this.cooldown = CooldownComponent(delay, freeze, execute)
    }

    /**
     * Builds the menu based on the provided configuration.
     *
     * @return A configured [Menu].
     */
    @PublishedApi
    internal open fun build(): Menu {
        return Menu(name, type, buttons, cooldown)
    }
}
