package net.craftoriya.packetuxui.menu.menu

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import kotlinx.coroutines.*
import net.craftoriya.packetuxui.api
import net.craftoriya.packetuxui.common.*
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.menu.button.Button
import net.craftoriya.packetuxui.menu.button.ButtonBuilder
import net.craftoriya.packetuxui.menu.button.ButtonBuilderDslMarker
import net.craftoriya.packetuxui.menu.button.click.ExecutableComponent
import net.craftoriya.packetuxui.menu.button.click.ExecutableComponentMarker
import net.craftoriya.packetuxui.user.AbstractUser
import net.craftoriya.packetuxui.user.User
import net.kyori.adventure.text.Component

typealias MenuJob = suspend CoroutineScope.() -> Unit

fun findMatchingMenu(user: User, containerId: Int) =
    menuService.menus.find { it.getContainerId(user) == containerId }

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

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@DslMarker
annotation class MenuBuilderDslMarker

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

    fun button(slot: Int, button: Button) {
        buttons[slot] = button
    }

    fun button(slot: Int, builder: @ButtonBuilderDslMarker ButtonBuilder.() -> Unit) {
        buttons[slot] = net.craftoriya.packetuxui.menu.button.button(builder)
    }

    fun buildAllButtons(builder: @ButtonBuilderDslMarker ButtonBuilder.(Int) -> Unit) {
        for (slot in 0 until type.size) {
            buttons[slot] = ButtonBuilder().apply { builder(slot) }.build()
        }
    }

    fun cooldown(cooldown: CooldownComponent) {
        this.cooldown = cooldown
    }

    fun cooldown(
        delay: Long = 0,
        freeze: Long = 0,
        execute: @ExecutableComponentMarker ExecutableComponent? = null
    ) {
        this.cooldown = CooldownComponent(delay, freeze, execute)
    }

    @PublishedApi
    internal fun build(): Menu {
        return Menu(name, type, buttons, cooldown)
    }
}
