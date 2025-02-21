package dev.slne.surf.gui.menu.menu

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import dev.slne.surf.gui.api
import dev.slne.surf.gui.common.*
import dev.slne.surf.gui.dto.CooldownComponent
import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.button.click.ExecutableComponent
import dev.slne.surf.gui.menu.button.click.ExecutableComponentMarker
import dev.slne.surf.gui.user.AbstractUser
import dev.slne.surf.gui.user.User
import kotlinx.coroutines.*
import net.kyori.adventure.text.Component

/**
 * A job that can be launched in a menu.
 */
typealias MenuJob = suspend CoroutineScope.() -> Unit

/**
 * Finds a menu that matches the given container ID.
 *
 * @param user The user to find the menu for.
 * @param containerId The container ID to match.
 * @return The matching menu, or null if no menu matches.
 */
fun findMatchingMenu(user: User, containerId: Int) =
    menuService.menus.find { it.getContainerId(user) == containerId }

/**
 * Represents a menu.
 *
 * @param name The name of the menu.
 * @param type The type of the menu.
 * @param buttons The buttons in the menu.
 * @param cooldown The cooldown of the menu.
 * @param coroutineScope The coroutine scope of the menu.
 */
open class Menu(
    val name: Component,
    val type: MenuType,
    buttons: Map<Int, Button>,
    val cooldown: CooldownComponent = CooldownComponent(), // TODO: Check this?
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    val buttons = mutableInt2ObjectMapOf(buttons).synchronize()
    val viewers = mutableObject2IntMapOf<User>().synchronize()

    private var menuScope = CoroutineScope(coroutineScope.coroutineContext + SupervisorJob())
    private val blocks = mutableObjectListOf<MenuJob>()
    private var shouldRelaunch = false

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

    /**
     * Opens the menu for the given user.
     *
     * @param user The user to open the menu for.
     */
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
        val containerId = getContainerId(user) ?: return

        val items = MutableList(type.size) { index ->
            this.buttons[index]?.item ?: ItemStack.EMPTY
        }

        user.sendPacket(WrapperPlayServerWindowItems(containerId, 0, items, carriedItem))
    }

    /**
     * Updates the slots in the menu for the given user.
     *
     * @param user The user to update the slots for.
     */
    fun updateSlots(user: User) {
        val containerId = getContainerId(user) ?: return

        for ((slot, button) in buttons) {
            user.sendPacket(WrapperPlayServerSetSlot(containerId, 0, slot, button.item))
        }
    }

    /**
     * Closes the menu for the given user.
     *
     * @param user The user to close the menu for.
     */
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

    /**
     * Destroys the menu.
     */
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

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@DslMarker
annotation class MenuBuilderDslMarker

/**
 * Creates a menu with the given type and builder.
 *
 * @param type The type of the menu.
 * @param builder The builder for the menu.
 */
inline fun menu(
    type: MenuType,
    builder: @MenuBuilderDslMarker MenuBuilderDsl.() -> Unit
): Menu {
    return MenuBuilderDsl(type).apply(builder).build()
}

@MenuBuilderDslMarker
class MenuBuilderDsl(val type: MenuType) {
    var name: Component = Component.empty()
    private val buttons = mutableInt2ObjectMapOf<Button>(type.size)
    private var cooldown = CooldownComponent()

    /**
     * Sets the button at the given slot.
     *
     * @param slot The slot to set the button at.
     * @param button The button to set.
     */
    fun button(slot: Int, button: Button) {
        buttons[slot] = button
    }

    /**
     * Sets the button at the given slot with the given builder.
     *
     * @param slot The slot to set the button at.
     * @param builder The builder for the button.
     */
    fun button(
        slot: Int,
        builder: @dev.slne.surf.gui.menu.button.ButtonBuilderDslMarker dev.slne.surf.gui.menu.button.ButtonBuilder.() -> Unit
    ) {
        buttons[slot] = dev.slne.surf.gui.menu.button.button(builder)
    }

    /**
     * Builds all buttons in the menu with the given builder.
     *
     * @param builder The builder for the buttons.
     */
    fun buildAllButtons(builder: @dev.slne.surf.gui.menu.button.ButtonBuilderDslMarker dev.slne.surf.gui.menu.button.ButtonBuilder.(Int) -> Unit) {
        for (slot in 0 until type.size) {
            buttons[slot] =
                dev.slne.surf.gui.menu.button.ButtonBuilder().apply { builder(slot) }.build()
        }
    }

    /**
     * Sets the cooldown of the menu.
     *
     * @param cooldown The cooldown to set.
     */
    fun cooldown(cooldown: CooldownComponent) {
        this.cooldown = cooldown
    }

    /**
     * Sets the cooldown of the menu with the given delay, freeze, and execute component.
     *
     * @param delay The delay of the cooldown.
     * @param freeze The freeze of the cooldown.
     * @param execute The execute component of the cooldown.
     */
    fun cooldown(
        delay: Long = 0,
        freeze: Long = 0,
        execute: @ExecutableComponentMarker ExecutableComponent? = null
    ) {
        this.cooldown = CooldownComponent(delay, freeze, execute)
    }

    /**
     * Builds the menu.
     *
     * @return The built menu.
     */
    @PublishedApi
    internal fun build(): Menu {
        return Menu(name, type, buttons, cooldown)
    }
}
