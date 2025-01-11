package dev.slne.packetuxui.menu.button.click

import com.github.retrooper.packetevents.protocol.item.ItemStack
import dev.slne.packetuxui.menu.button.ButtonType
import dev.slne.packetuxui.menu.menu.Menu
import dev.slne.packetuxui.user.User

@DslMarker
@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
annotation class ExecutableComponentMarker

typealias ExecutableComponent = (ExecuteComponent) -> Unit

data class ExecuteComponent(
    val user: User,
    val buttonType: ButtonType,
    val slot: Int,
    val itemStack: ItemStack?,
    val menu: Menu
)
