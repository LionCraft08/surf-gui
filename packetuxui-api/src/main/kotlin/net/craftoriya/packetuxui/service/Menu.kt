package net.craftoriya.packetuxui.service

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems
import kotlinx.coroutines.*
import net.craftoriya.packetuxui.common.mutableInt2ObjectMapOf
import net.craftoriya.packetuxui.common.mutableObjectListOf
import net.craftoriya.packetuxui.common.synchronize
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.types.ExecutableComponent
import net.craftoriya.packetuxui.types.ExecutableComponentMarker
import net.craftoriya.packetuxui.types.InventoryType
import net.craftoriya.packetuxui.user.User
import net.kyori.adventure.text.Component

typealias MenuJob = suspend CoroutineScope.() -> Unit

open class Menu(
    val name: Component,
    val type: InventoryType,
    buttons: Map<Int, Button>,
    val cooldown: CooldownComponent = CooldownComponent(),
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    val buttons = mutableInt2ObjectMapOf(buttons).synchronize()
    var needsRescope = false

    @Volatile
    var contentPacket: WrapperPlayServerWindowItems

    @Volatile
    var menuPacket: WrapperPlayServerOpenWindow

    private var menuScope = CoroutineScope(coroutineScope.coroutineContext + SupervisorJob())
    private val blocks = mutableObjectListOf<MenuJob>()

    fun launchJob(block: MenuJob) {
        menuScope.launch {
            block()
        }

        blocks.add(block)
    }

    fun open(user: User) {
        if (needsRescope) {
            needsRescope = false
            
            for (block in blocks) {
                menuScope.launch {
                    block()
                }
            }
        }

        user.sendPacket(menuPacket)
        user.sendPacket(contentPacket)
    }

    fun close() {
        menuScope.cancel()
        needsRescope = true
    }

    fun copy(): Menu {
        return Menu(name, type, buttons, cooldown)
    }

    init {
        val items = MutableList(type.size) { index ->
            this.buttons[index]?.item ?: ItemStack.EMPTY
        }
        check(buttons.size <= type.size) { "Too many items in menu" }

        menuPacket = WrapperPlayServerOpenWindow(126, type.id(), name)
        contentPacket = WrapperPlayServerWindowItems(126, 0, items, null)
    }
}

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@DslMarker
annotation class MenuBuilderDslMarker

inline fun menu(
    type: InventoryType,
    builder: @MenuBuilderDslMarker MenuBuilderDsl.() -> Unit
): Menu {
    return MenuBuilderDsl(type).apply(builder).build()
}

@MenuBuilderDslMarker
class MenuBuilderDsl(val type: InventoryType) {
    var name: Component = Component.empty()
    private val buttons = mutableInt2ObjectMapOf<Button>(type.size)
    private var cooldown = CooldownComponent()

    fun button(slot: Int, button: Button) {
        buttons[slot] = button
    }

    fun button(slot: Int, builder: @ButtonBuilderDslMarker ButtonBuilder.() -> Unit) {
        buttons[slot] = button(builder)
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
