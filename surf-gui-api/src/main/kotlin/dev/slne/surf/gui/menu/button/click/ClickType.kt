package dev.slne.surf.gui.menu.button.click

/**
 * Represents the type of click that was performed by the player.
 */
enum class ClickType {
    /**
     * Represents a pick up click.
     */
    PICKUP,

    /**
     * Represents a place click.
     */
    PLACE,

    /**
     * Represents a shift click.
     */
    SHIFT_CLICK,

    /**
     * Represents a drag start.
     */
    DRAG_START,

    /**
     * Represents a drag add.
     */
    DRAG_ADD,

    /**
     * Represents a drag end.
     */
    DRAG_END,

    /**
     * Represents a pickup all.
     */
    PICKUP_ALL,

    /**
     * Represents an undefined click.
     */
    UNDEFINED
}