package dev.slne.surf.gui.dto

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import dev.slne.surf.gui.menu.button.click.ClickType

/**
 * Represents a drag that has been accumulated.
 *
 * @param packet The packet that was sent.
 * @param type The type of click that was sent.
 */
data class AccumulatedDrag(
    val packet: WrapperPlayClientClickWindow,
    val type: ClickType
)