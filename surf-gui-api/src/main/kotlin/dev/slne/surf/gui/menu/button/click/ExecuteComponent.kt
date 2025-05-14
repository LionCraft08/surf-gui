package dev.slne.surf.gui.menu.button.click

import com.github.retrooper.packetevents.protocol.item.ItemStack
import dev.slne.surf.gui.menu.button.ButtonType
import dev.slne.surf.gui.menu.menu.Menu
import dev.slne.surf.gui.user.User

@DslMarker
@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
annotation class ExecutableComponentMarker

/**
 * Represents a component that can be executed when a button is clicked.
 */
typealias ExecutableComponent = (ExecuteComponent) -> Unit
typealias TextExecutableComponent = (TextExecuteComponent) -> Unit
/**
 * Represents a component that can be executed when a button is clicked.
 *
 * @param user The user that clicked the button.
 * @param buttonType The type of the button that was clicked.
 * @param slot The slot of the button that was clicked.
 * @param itemStack The item stack of the button that was clicked.
 * @param menu The menu that the button belongs to.
 */
data class ExecuteComponent(
    val user: User,
    val buttonType: ButtonType,
    val slot: Int,
    val itemStack: ItemStack?,
    val menu: Menu
)
data class TextExecuteComponent(
    val user: User,
    val buttonType: ButtonType,
    val slot: Int,
    val itemStack: ItemStack?,
    val menu: Menu,
    val text: String
)