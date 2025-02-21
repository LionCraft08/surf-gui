package dev.slne.surf.gui.menu.button.click

import dev.slne.surf.gui.menu.button.ButtonType

/**
 * Represents the type of click that was performed.
 *
 * @property buttonType The type of button that was clicked.
 * @property clickType The type of click that was performed.
 */
data class ClickData(
    val buttonType: ButtonType,
    val clickType: ClickType
)
