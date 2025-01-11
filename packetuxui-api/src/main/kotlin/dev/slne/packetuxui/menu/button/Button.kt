package dev.slne.packetuxui.menu.button

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.item.ItemStack
import dev.slne.packetuxui.common.toPlain
import dev.slne.packetuxui.dto.CooldownComponent
import dev.slne.packetuxui.menu.button.click.ExecutableComponent
import dev.slne.packetuxui.menu.button.click.ExecuteComponent
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