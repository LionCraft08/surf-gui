package dev.slne.surf.gui.menu.button

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.item.ItemStack
import dev.slne.surf.gui.common.toPlain
import dev.slne.surf.gui.dto.CooldownComponent
import dev.slne.surf.gui.menu.button.click.ExecutableComponent
import dev.slne.surf.gui.menu.button.click.ExecuteComponent
import dev.slne.surf.gui.menu.item.ItemBuilder
import net.kyori.adventure.text.Component

/**
 * Represents a button in a menu
 *
 * @param item The item that represents the button
 * @param execute The function that will be executed when the button is clicked
 * @param cooldown The cooldown of the button
 */
open class Button(
    var item: ItemStack,
    var execute: ExecutableComponent? = null,
    var cooldown: CooldownComponent = CooldownComponent(0)
) {
    /**
     * Executes the button's function
     *
     * @param executeComponent The component that contains the information about the click
     */
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

    fun item(item: ItemStack):ButtonDslBuilder {
        this.item = item
        return this
    }

    fun item(builder: @ButtonBuilderDslMarker ItemStack.Builder.() -> Unit):ButtonDslBuilder {
        item(ItemStack.builder().apply(builder).build())
        return this
    }

    fun itemBuilder(builder: @ButtonBuilderDslMarker ItemBuilder.() -> Unit):ButtonDslBuilder {
        item(ItemBuilder().apply(builder).build())
        return this
    }

    fun onClick(click: @ButtonBuilderDslMarker ExecutableComponent):ButtonDslBuilder {
        this.click = click
        return this
    }


    fun cooldown(cooldown: CooldownComponent):ButtonDslBuilder {
        this.cooldown = cooldown
        return this
    }

    fun cooldown(
        delay: Long = 0,
        freeze: Long = 0,
        execute: @ButtonBuilderDslMarker ExecutableComponent? = null
    ):ButtonDslBuilder {
        cooldown(CooldownComponent(delay, freeze, execute))
        return this
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