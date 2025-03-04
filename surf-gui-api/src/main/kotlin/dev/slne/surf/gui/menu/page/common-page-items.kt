package dev.slne.surf.gui.menu.page

import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import dev.slne.surf.gui.menu.item.ItemBuilder
import net.kyori.adventure.text.Component

internal fun createNextPageStack() = ItemBuilder().apply {
    itemType = ItemTypes.ARROW
    name = Component.text("Nächste Seite")
}.build()

internal fun createPreviousPageStack() = ItemBuilder().apply {
    itemType = ItemTypes.ARROW
    name = Component.text("Vorherige Seite")
}.build()