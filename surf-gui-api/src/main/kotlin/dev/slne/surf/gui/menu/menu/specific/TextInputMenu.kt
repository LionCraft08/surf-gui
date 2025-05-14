package dev.slne.surf.gui.menu.menu.specific

import dev.slne.surf.gui.common.mutableInt2ObjectMapOf
import dev.slne.surf.gui.common.toComponent
import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.button.buttons.TextInputConfirmButton
import dev.slne.surf.gui.menu.button.click.ExecutableComponent
import dev.slne.surf.gui.menu.button.click.ExecuteComponent
import dev.slne.surf.gui.menu.button.click.TextExecutableComponent
import dev.slne.surf.gui.menu.item.CustomItemProvider
import dev.slne.surf.gui.menu.menu.DefaultMenu
import dev.slne.surf.gui.menu.menu.Menu
import dev.slne.surf.gui.menu.menu.MenuType
import net.kyori.adventure.text.Component

class TextInputMenu(name: Component, onEnter: TextExecutableComponent, var text: String = "") : DefaultMenu(name, MenuType.ANVIL, mutableInt2ObjectMapOf(), "textinput") {
    init {
        setButton(2, TextInputConfirmButton(onEnter))
        setButton(1, Button(CustomItemProvider.getDeclineButton(),{
            e -> e.user.openPreviousMenu()
        }))
        setButton(0, Button(CustomItemProvider("block", false,
            text.toComponent()).build(),{}))

    }
}