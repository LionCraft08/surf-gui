package dev.slne.surf.gui.menu.menu

import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.overlays.MenuOverlayProvider
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

open class DefaultMenu(
    name: Component,
    type: MenuType,
    buttons: Int2ObjectMap<Button>,
    var overlay:String="default"
) : Menu(
    MenuOverlayProvider.getOverlayComponent(overlay).append(name).colorIfAbsent(NamedTextColor.GRAY),
    type, buttons
) {
    init {

    }
}