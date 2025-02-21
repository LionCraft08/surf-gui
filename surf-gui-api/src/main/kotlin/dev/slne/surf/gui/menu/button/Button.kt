package dev.slne.surf.gui.menu.button

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.item.ItemStack
import dev.slne.surf.gui.common.toPlain
import dev.slne.surf.gui.dto.CooldownComponent
import dev.slne.surf.gui.menu.button.click.ExecutableComponent
import dev.slne.surf.gui.menu.button.click.ExecuteComponent
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