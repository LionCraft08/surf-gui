package dev.slne.surf.gui.menu.button.buttons

import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.button.click.TextExecutableComponent
import dev.slne.surf.gui.menu.button.click.TextExecuteComponent
import dev.slne.surf.gui.menu.item.CustomItemProvider

class TextInputConfirmButton(val textExecute: TextExecutableComponent): Button(CustomItemProvider.getConfirmButton()) {

}