package net.craftoriya.packetuxui.menu.button

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.common.toPlain
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.menu.button.click.ExecutableComponent
import net.craftoriya.packetuxui.menu.button.click.ExecuteComponent
import net.craftoriya.packetuxui.menu.item.ItemBuilder
import net.kyori.adventure.text.Component

open class Button(
    var item: ItemStack,
    var execute: ExecutableComponent? = null,
    var cooldown: CooldownComponent = CooldownComponent(0)

) {
    open fun onClick(executeComponent: ExecuteComponent) {
        execute?.invoke(executeComponent)
    }

    override fun toString(): String {
        return "Button(item=${item.type.name}|${
            item.getComponentOr(
                ComponentTypes.ITEM_NAME,
                Component.text("undefined")
            ).toPlain()
        })"
    }
}

@ButtonBuilderDslMarker
open class ButtonDslBuilder() {
    private var item = ItemStack.EMPTY
    private var click: ExecutableComponent? = null
    private var cooldown = CooldownComponent.EMPTY

    fun item(item: ItemStack) {
        this.item = item
    }

    fun item(builder: @ButtonBuilderDslMarker ItemStack.Builder.() -> Unit) {
        item(ItemStack.builder().apply(builder).build())
    }

    fun itemBuilder(builder: @ButtonBuilderDslMarker ItemBuilder.() -> Unit) {
        item(ItemBuilder().apply(builder).build())
    }

    fun onClick(click: @ButtonBuilderDslMarker ExecutableComponent) {
        this.click = click
    }


    fun cooldown(cooldown: CooldownComponent) {
        this.cooldown = cooldown
    }

    fun cooldown(
        delay: Long = 0,
        freeze: Long = 0,
        execute: @ButtonBuilderDslMarker ExecutableComponent? = null
    ) {
        cooldown(CooldownComponent(delay, freeze, execute))
    }

    internal open fun build(): Button {
        return Button(item, click, cooldown)
    }
}

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@DslMarker
annotation class ButtonBuilderDslMarker

fun Button(builder: @ButtonBuilderDslMarker ButtonDslBuilder.() -> Unit): Button =
    ButtonDslBuilder().apply(builder).build()