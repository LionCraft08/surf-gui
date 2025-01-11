package dev.slne.packetuxui.menu.button.buttons

import dev.slne.packetuxui.menu.button.Button
import dev.slne.packetuxui.menu.button.click.ExecuteComponent

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

    fun switchState() {
        val currentIndex = states.indexOf(currentState)

        currentState = states[(currentIndex + 1) % states.size]
    }
}