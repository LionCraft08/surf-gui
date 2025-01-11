package dev.slne.menus

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import dev.slne.packetuxui.bukkit.extensions.toPlayer
import dev.slne.packetuxui.menu.button.Button
import dev.slne.packetuxui.menu.item.ItemBuilder
import dev.slne.packetuxui.menu.menu.MenuType
import dev.slne.packetuxui.menu.page.PaginatedMenu
import dev.slne.packetuxui.menu.utils.position
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
        dev.slne.menus.generateButtons(100),
        position(0, 1)..position(8, 3),
        position(0, 4),
        position(8, 4),
    ) {
    init {
        for (slot in 0 until type.size) {
            when (slot) {
                in position(0, 0).toSlot()..position(8, 0).toSlot() ->
                    buttons[slot] = Button(
                        item = ItemStack.builder().type(ItemTypes.BLACK_STAINED_GLASS_PANE).build()
                    )

                in position(1, 4).toSlot()..position(7, 4).toSlot() ->
                    buttons[slot] = Button(
                        item = ItemStack.builder().type(ItemTypes.BLACK_STAINED_GLASS_PANE).build()
                    )
            }
        }
    }
}