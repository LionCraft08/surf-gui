package dev.slne.surf.gui.menu.menu.specific

import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import dev.slne.surf.gui.common.int2ObjectMapOf
import dev.slne.surf.gui.common.mutableInt2ObjectMapOf
import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.item.CustomItemProvider
import dev.slne.surf.gui.menu.item.ItemBuilder
import dev.slne.surf.gui.menu.menu.DefaultMenu
import dev.slne.surf.gui.menu.menu.MenuType
import dev.slne.surf.gui.util.Slot
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.kyori.adventure.text.Component

class TestMenu: ScrollableMenu(
    Component.text("Spieler"),
    getItems(),
    CustomItemProvider("plus").toButton{
        (user, buttonType, slot, itemStack, menu) ->
        user.sendMessage(Component.text("Adding a new Player..."))
    }) {
    init {

    }
}
fun getItems(): Int2ObjectOpenHashMap<Button> {
    val map = mutableInt2ObjectMapOf<Button>()
    for (i in IntRange(0, 100))
        map.put(map.size,
            Button(
            ItemBuilder().itemType(ItemTypes.PAPER)
            .name(Component.text("Player $i"))
            .amount(1).build()
        ))
    return map
}
