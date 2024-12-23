package net.craftoriya.packetuxui.menu.button

import com.github.retrooper.packetevents.protocol.item.ItemStack
import net.craftoriya.packetuxui.dto.CooldownComponent
import net.craftoriya.packetuxui.menu.button.click.ExecutableComponent

data class Button(
    var item: ItemStack,
    val execute: ExecutableComponent? = null,
    val cooldown: CooldownComponent
)