package dev.slne.surf.gui.menu.menu.specific

import dev.slne.surf.gui.common.int2ObjectMapOf
import dev.slne.surf.gui.common.mutableInt2ObjectMapOf
import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.button.click.ExecutableComponent
import dev.slne.surf.gui.menu.button.click.ExecuteComponent
import dev.slne.surf.gui.menu.item.CustomItemProvider
import dev.slne.surf.gui.menu.menu.DefaultMenu
import dev.slne.surf.gui.menu.menu.Menu
import dev.slne.surf.gui.menu.menu.MenuType
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

class ConfirmMenu(onConfirm: ExecutableComponent, onDecline: ExecutableComponent = {data->data.user.openPreviousMenu()}) : DefaultMenu(
    Component.text("Bestätigen?"),
    MenuType.GENERIC9X4,
    mutableInt2ObjectMapOf<Button>(
        Pair(20, Button(CustomItemProvider.getConfirmButton(), onConfirm)),
        Pair(24, Button(CustomItemProvider.getDeclineButton(), onDecline))
    ),
    "confirm"
    ) {

}



