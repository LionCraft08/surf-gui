package net.craftoriya.packetuxui.dto

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import net.craftoriya.packetuxui.menu.button.click.ClickType

data class AccumulatedDrag(
    val packet: WrapperPlayClientClickWindow,
    val type: ClickType
)