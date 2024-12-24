package net.craftoriya.menus

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import net.craftoriya.packetuxui.bukkit.extensions.toPlayer
import net.craftoriya.packetuxui.menu.button.Button
import net.craftoriya.packetuxui.menu.item.ItemBuilder
import net.craftoriya.packetuxui.menu.menu.MenuType
import net.craftoriya.packetuxui.menu.menu.page.PaginatedMenu
import net.craftoriya.packetuxui.menu.utils.slot
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component

fun generateButtons(amount: Int): List<Button> {
    return (0 until amount).map { index ->
        Button(
            item = ItemBuilder().itemType(ItemTypes.ACACIA_BOAT).name(Component.text("$index"))
                .build(),
            execute = {
                it.user.toPlayer()?.playSound(Sound.sound { builder ->
                    builder.type(org.bukkit.Sound.BLOCK_ANVIL_FALL)
                    builder.volume(.5f)
                })

                it.user.sendMessage(Component.text("You clicked on button $index"))
            }
        )
    }
}

class PaginatedMenuTest :
    PaginatedMenu(
        Component.text("Test"),
        MenuType.GENERIC9X5,
        generateButtons(100),
        slot(0, 1)..slot(8, 3),
        slot(0, 4),
        slot(8, 4),
    ) {
    init {
        for (slot in 0 until type.size) {
            when (slot) {
                in slot(0, 0).toSlot()..slot(8, 0).toSlot() ->
                    buttons[slot] = Button(
                        item = ItemStack.builder().type(ItemTypes.BLACK_STAINED_GLASS_PANE).build()
                    )

                in slot(1, 4).toSlot()..slot(7, 4).toSlot() ->
                    buttons[slot] = Button(
                        item = ItemStack.builder().type(ItemTypes.BLACK_STAINED_GLASS_PANE).build()
                    )
            }
        }
    }
}