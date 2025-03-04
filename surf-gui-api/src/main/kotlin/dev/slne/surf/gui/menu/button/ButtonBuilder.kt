package dev.slne.surf.gui.menu.button

import com.github.retrooper.packetevents.protocol.item.ItemStack
import dev.slne.surf.gui.dto.CooldownComponent
import dev.slne.surf.gui.menu.button.click.ExecutableComponent
import dev.slne.surf.gui.menu.button.click.ExecutableComponentMarker
import dev.slne.surf.gui.menu.item.ItemBuilder

/**
 * A builder for [Button]s.
 */
@Deprecated("Use the DSL instead", ReplaceWith("Button { }", "dev.slne.surf.gui.menu.button.Button"))
class ButtonBuilder {

    private var item: ItemStack = ItemStack.EMPTY
    private var click: ExecutableComponent? = null
    private var cooldown: CooldownComponent = CooldownComponent(0)
    private var commands: Array<String>? = null
    private var playerCommands: Array<String>? = null

    /**
     * Set the item of the button.
     *
     * @param item The item to set.
     * @return The builder.
     */
    fun item(item: ItemStack) = apply { this.item = item }

    /**
     * Set the click action of the button.
     *
     * @param click The click action to set.
     * @return The builder.
     */
    fun click(click: ExecutableComponent) = apply { this.click = click }

    /**
     * Set the commands to execute when the button is clicked.
     *
     * @param commands The commands to execute.
     * @return The builder.
     */
    fun executeCommand(commands: Array<String>) = apply { this.commands = commands }

    /**
     * Set the player commands to execute when the button is clicked.
     *
     * @param playerCommands The player commands to execute.
     * @return The builder.
     */
    fun makePlayerExecuteCommand(command: Array<String>) =
        apply { this.playerCommands = command }

    /**
     * Set the cooldown of the button.
     *
     * @param cooldown The cooldown to set.
     * @return The builder.
     */
    fun cooldown(cooldown: CooldownComponent) = apply { this.cooldown = cooldown }

    /**
     * Build the button.
     *
     * @return The built button.
     */
    fun build() = Button(item, click, cooldown)

    /**
     * Set the cooldown of the button.
     *
     * @param delay The delay of the cooldown.
     * @param freeze The freeze of the cooldown.
     * @param execute The action to execute when the cooldown is over.
     * @return The builder.
     */
    fun cooldown(
        delay: Long = 0,
        freeze: Long = 0,
        execute: @ExecutableComponentMarker ExecutableComponent? = null
    ) = cooldown(CooldownComponent(delay, freeze, execute))

    /**
     * Set the item of the button.
     *
     * @param builder The builder for the item.
     * @return The builder.
     */
    fun item(builder: ItemStack.Builder.() -> Unit) =
        item(ItemStack.builder().apply(builder).build())

    /**
     * Set the item of the button.
     *
     * @param builder The builder for the item.
     * @return The builder.
     */
    fun buildItem(builder: ItemBuilder.() -> Unit) =
        item(ItemBuilder().apply(builder).build())
}
