package net.craftoriya.packetuxui.menu.button

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.menu.button.click.ExecutableComponent
import net.craftoriya.packetuxui.menu.button.click.ExecutableComponentMarker
import net.craftoriya.packetuxui.menu.item.ItemBuilder

@ButtonBuilderDslMarker
class ButtonBuilder {

    private var item: ItemStack = ItemStack.EMPTY
    private var click: ExecutableComponent? = null
    private var cooldown: CooldownComponent = CooldownComponent(0)
    private var commands: Array<String>? = null
    private var playerCommands: Array<String>? = null

    fun item(item: ItemStack) = apply { this.item = item }
    fun click(click: ExecutableComponent) = apply { this.click = click }
    fun executeCommand(command: Array<String>) = apply { this.commands = command }

    fun makePlayerExecuteCommand(command: Array<String>) =
        apply { this.playerCommands = command }

    fun cooldown(cooldown: CooldownComponent) = apply { this.cooldown = cooldown }

    fun build() = Button(item, click, cooldown)

    fun cooldown(
        delay: Long = 0,
        freeze: Long = 0,
        execute: @ExecutableComponentMarker ExecutableComponent? = null
    ) = cooldown(CooldownComponent(delay, freeze, execute))

    fun item(builder: ItemStack.Builder.() -> Unit) =
        item(ItemStack.builder().apply(builder).build())

    fun buildItem(builder: ItemBuilder.() -> Unit) =
        item(ItemBuilder().apply(builder).build()) // TODO: meaningfully name

}

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@DslMarker
annotation class ButtonBuilderDslMarker

fun Button(builder: @ButtonBuilderDslMarker ButtonBuilder.() -> Unit): Button =
    ButtonBuilder().apply(builder).build()
