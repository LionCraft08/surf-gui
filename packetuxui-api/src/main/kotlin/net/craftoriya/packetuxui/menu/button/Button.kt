package net.craftoriya.packetuxui.menu.button

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.common.toPlain
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.menu.button.click.ExecutableComponent
import net.craftoriya.packetuxui.menu.button.click.ExecuteComponent
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