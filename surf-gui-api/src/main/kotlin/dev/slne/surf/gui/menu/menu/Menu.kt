package dev.slne.surf.gui.menu.menu

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenSignEditor
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowProperty
import dev.slne.surf.gui.SurfGuiApi
import dev.slne.surf.gui.api
import dev.slne.surf.gui.common.*
import dev.slne.surf.gui.dto.CooldownComponent
import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.button.ButtonBuilderDslMarker
import dev.slne.surf.gui.menu.button.ButtonDslBuilder
import dev.slne.surf.gui.menu.button.buttons.SwitchButton
import dev.slne.surf.gui.menu.button.buttons.SwitchButtonDslBuilder
import dev.slne.surf.gui.menu.button.click.ExecutableComponent
import dev.slne.surf.gui.menu.button.click.ExecutableComponentMarker
import dev.slne.surf.gui.user.AbstractUser
import dev.slne.surf.gui.user.User
import dev.slne.surf.gui.util.Slot
import dev.slne.surf.gui.util.SlotRange
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import kotlinx.coroutines.*
import net.kyori.adventure.text.Component
import org.jetbrains.annotations.NotNull

/**
 * A job that can be launched in a menu.
 */
typealias MenuJob = suspend CoroutineScope.() -> Unit

/**
 * Represents a menu.
 *
 * @param name The name of the menu.
 * @param type The type of the menu.
 * @param buttons The buttons in the menu.
 * @param cooldown The cooldown of the menu.
 * @param coroutineScope The coroutine scope of the menu.
 */
abstract class Menu(
    protected open val name: Component,
    val type: MenuType,
    buttons: Int2ObjectMap<Button>,
    var cooldown: CooldownComponent = CooldownComponent.EMPTY,
    private val coroutineScope: CoroutineScope = CoroutineScope(
        Dispatchers.Default + CoroutineName(
            "Menu-${name.toPlain()}"
        )
    )
) {
    val buttons = mutableInt2ObjectMapOf(buttons).synchronize()
    val viewers = mutableObjectListOf<User>().synchronize()


    private var menuScope = CoroutineScope(coroutineScope.coroutineContext + SupervisorJob())
    private val blocks = mutableObjectListOf<MenuJob>()
    private var shouldRelaunch = false
    var registerInStackTrace = true

    init {
        check(buttons.size <= type.size) { "Too many items in menu" }
    }

    /**
     * Launches a job in the menu.
     *
     * @param block The job to launch.
     */
    fun launchJob(block: MenuJob) {
        menuScope.launch { block() }
        blocks.add(block)
    }

    fun setButton(int: Int, button: Button?){
        if(button == null) buttons.remove(int)
        else buttons.put(int, button)
    }

    fun setButton(slot: Slot, button: Button?) = setButton(slot.toSlot(), button)

    /**
     * Opens the menu for the given user.
     *
     * @param user The user to open the menu for.
     */
     fun open(user: User) {
        if (shouldRelaunch) {
            shouldRelaunch = false
            menuScope = CoroutineScope(coroutineScope.coroutineContext + SupervisorJob())

            for (block in blocks) {
                menuScope.launch { block() }
            }
        }

        viewers.add(user)
        (user as AbstractUser).setActiveMenu(this)
        user.sendPacket(WrapperPlayServerOpenWindow(user.getNextContainerID(), type.id(), name))
        sendWindowItems(user)

    }

    fun updateItem(user: User, slot: Slot, item: ItemStack) = updateItem(user, slot.toSlot(), item)

    /**
     * Updates the item in the menu at the given slot.
     *
     * @param user The user to update the item for.
     * @param slot The slot to update the item at.
     */
    fun updateItem(user: User, slot: Int, item: ItemStack) {
        buttons[slot].item = item

        sendWindowItems(user)
    }


    /**
     * Updates the button in the menu at the given slot.
     *
     * @param user The user to update the button for.
     * @param slot The slot to update the button at.
     */
    fun updateButton(user: User, slot: Int, button: Button) {
        buttons[slot] = button

        sendWindowItems(user)
    }

    /**
     * Sends the window items to the given user.
     *
     * @param user The user to send the window items to.
     * @param carriedItem The item the user is carrying.
     */
    fun sendWindowItems(user: User, carriedItem: ItemStack? = null) {

        val items = MutableList(type.size) { index ->
            this.buttons[index]?.item ?: ItemStack.EMPTY
        }

        SurfGuiApi.getInstance().log("Sending Items to player: "+items.size, System.Logger.Level.DEBUG)
        user.sendPacket(WrapperPlayServerWindowItems(user.getCurrentContainerID(), 0, items, carriedItem))
    }

    /**
     * Updates the slots in the menu for the given user.
     * @param user The user to update the slots for.
     */
    fun updateSlots(user: User) {
        for ((slot, button) in buttons) {
            user.sendPacket(WrapperPlayServerSetSlot(0, 0, slot, button.item))
        }
    }

    /**
     * Closes the menu for the given user.
     *
     * @param user The user to close the menu for.
     */
     fun close(user: User) {
        viewers.remove(user)

        (user as AbstractUser).setActiveMenu(null)


        if (viewers.isEmpty()) {
            destroy()
        }

        user.sendPacket(WrapperPlayServerCloseWindow())

        //Resending the Player its Inv Items, necessary until item split drags are fixed
        user.updateInventory()
    }

    /**
     * Destroys the menu.
     */
    private fun destroy() {
        menuScope.cancel()
        shouldRelaunch = true
        SurfGuiApi.getInstance().log("Destroying menu ${name.toPlain()}", System.Logger.Level.DEBUG)
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
    private val menuButtons = mutableInt2ObjectMapOf<Button>(type.size)
    private var cooldown = CooldownComponent.EMPTY
    val buttons = ButtonContext(type, this)

    /**
     * Add a button at a specific slot.
     *
     * @param slot The slot index where the button will be placed.
     * @param button The button to add.
     */
    fun button(slot: Int, button: Button) {
        menuButtons[slot] = button
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
    fun button(slot: Int, builder: @MenuBuilderDslMarker ButtonDslBuilder.() -> Unit) =
        button(slot, Button(builder))

    /**
     * Add a button at a specific [Slot] using a builder lambda.
     *
     * @param slot The slot where the button will be placed.
     * @param builder The builder lambda for the button.
     */
    fun button(slot: Slot, builder: @MenuBuilderDslMarker ButtonDslBuilder.() -> Unit) =
        button(slot.toSlot(), builder)

    /**
     * Add multiple buttons for a given slot range.
     *
     * @param range The [SlotRange] where buttons will be placed.
     * @param builder The builder lambda for each slot in the range.
     */
    fun buttons(range: SlotRange, builder: @MenuBuilderDslMarker ButtonDslBuilder.(Slot) -> Unit) {
        require(range.compatibleWith(type)) { "Slot range is not compatible with menu type" }

        for (slot in range) {
            menuButtons[slot.toSlot()] = ButtonDslBuilder().apply { builder(slot) }.build()
        }
    }

    fun buttons(slots: List<Slot>, builder: @MenuBuilderDslMarker ButtonDslBuilder.(Slot) -> Unit) {
        for (slot in slots) {
            menuButtons[slot.toSlot()] = ButtonDslBuilder().apply { builder(slot) }.build()
        }
    }

    fun switchButton(slot: Slot, button: SwitchButton) = button(slot, button)
    fun switchButton(slot: Slot, builder: @MenuBuilderDslMarker SwitchButtonDslBuilder.() -> Unit) =
        switchButton(slot, SwitchButton(builder))
    fun switchButton(slot: Int, builder: @MenuBuilderDslMarker SwitchButtonDslBuilder.() -> Unit) =
        switchButton(Slot(slot), builder)
    fun switchButton(slot: Int, button: SwitchButton) = switchButton(Slot(slot), button)
    fun switchButtons(range: SlotRange, builder: @MenuBuilderDslMarker SwitchButtonDslBuilder.(Slot) -> Unit) {
        require(range.compatibleWith(type)) { "Slot range is not compatible with menu type" }

        for (slot in range) {
            menuButtons[slot.toSlot()] = SwitchButtonDslBuilder().apply { builder(slot) }.build()
        }
    }
    fun switchButtons(slots: List<Slot>, builder: @MenuBuilderDslMarker SwitchButtonDslBuilder.(Slot) -> Unit) {
        for (slot in slots) {
            menuButtons[slot.toSlot()] = SwitchButtonDslBuilder().apply { builder(slot) }.build()
        }
    }
    fun switchButtons(range: SlotRange, buttons: List<SwitchButton>) {
        require(range.compatibleWith(type)) { "Slot range is not compatible with menu type" }

        for ((index, slot) in range.withIndex()) {
            menuButtons[slot.toSlot()] = buttons[index]
        }
    }

    /**
     * Add buttons to all available slots.
     *
     * @param builder The builder lambda for each slot.
     */
    fun buildAllButtons(builder: @MenuBuilderDslMarker ButtonDslBuilder.(Slot) -> Unit) {
        buttons(SlotRange(Slot(0), Slot(type.size - 1)), builder)
    }

    fun fillEmptyButtons(builder: @MenuBuilderDslMarker ButtonDslBuilder.(Slot) -> Unit) {
        for (slot in 0 until type.size) {
            if (menuButtons[slot] == null) {
                menuButtons[slot] = ButtonDslBuilder().apply { builder(Slot(slot)) }.build()
            }
        }
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
        return DefaultMenu(name, type, menuButtons).also { it.cooldown = cooldown }
    }
}


@MenuBuilderDslMarker
class ButtonContext(private val type: MenuType, private val builder: MenuBuilderDsl) {
    private var range: SlotRange = SlotRange(Slot(0), Slot(type.size - 1))

    infix fun where(predicate: Slot.() -> Boolean): ButtonContext {
        val filteredSlots = range.filter { it.predicate() }
        range = SlotRange(filteredSlots.first(), filteredSlots.last())
        return this
    }

    infix fun whereX(expectedX: Int): ButtonContext = where { x == expectedX }
    infix fun whereY(expectedY: Int): ButtonContext = where { y == expectedY }

    infix fun build(builderAction: @ButtonBuilderDslMarker ButtonDslBuilder.(Slot) -> Unit) {
        require(range.compatibleWith(type)) { "Slot range is not compatible with menu type" }

        for (slot in range) {
            builder.button(slot) {
                builderAction(slot)
            }
        }
    }
}

