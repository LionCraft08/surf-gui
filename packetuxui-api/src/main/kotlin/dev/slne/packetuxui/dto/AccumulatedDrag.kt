package dev.slne.packetuxui.dto

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import dev.slne.packetuxui.menu.button.click.ClickType

data class AccumulatedDrag(
    val packet: WrapperPlayClientClickWindow,
    val type: ClickType
)