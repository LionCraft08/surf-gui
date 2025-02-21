package dev.slne.surf.gui.menu.button.buttons

import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.button.click.ExecuteComponent

/**
 * A button that can switch between multiple states.
 *
 * @param states The list of states that this button can switch between.
 * @param defaultState The default state of this button.
 * @param onStateChange A callback that is called when the state of this button changes.
 */
class SwitchButton(
    val states: List<Button>,
    val defaultState: Button,
    val onStateChange: ((fromState: Button, toState: Button) -> Unit)? = null
) : Button(
    item = defaultState.item,
    execute = defaultState.execute,
    cooldown = defaultState.cooldown
) {
    var currentState = defaultState
        private set

    /**
     * Switches the state of this button.
     * This method will also update the item, execute, and cooldown of this button.
     *
     * @param executeComponent The [ExecuteComponent] that triggered this button.
     */
    override fun onClick(executeComponent: ExecuteComponent) {
        val oldState = currentState
        switchState()

        onStateChange?.invoke(oldState, currentState)
        super.onClick(executeComponent)

        item = currentState.item
        execute = currentState.execute
        cooldown = currentState.cooldown

        executeComponent.menu.updateButton(
            executeComponent.user, executeComponent.slot, this
        )
    }

    /**
     * Switches the state of this button.
     */
    fun switchState() {
        val currentIndex = states.indexOf(currentState)

        currentState = states[(currentIndex + 1) % states.size]
    }
}