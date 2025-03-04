package dev.slne.surf.gui.menu.button.buttons

import dev.slne.surf.gui.common.mutableObjectListOf
import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.button.ButtonBuilderDslMarker
import dev.slne.surf.gui.menu.button.ButtonDslBuilder
import dev.slne.surf.gui.menu.button.click.ExecuteComponent

typealias SwitchButtonStateChangeHandler = (fromState: Button, toState: Button) -> Unit

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
    val onStateChange: SwitchButtonStateChangeHandler? = null
) : Button(
    item = defaultState.item,
    execute = defaultState.execute,
    cooldown = defaultState.cooldown
) {
    init {
        check(states.size > 1) { "SwitchButton must have at least 2 states" }
        check(states.contains(defaultState)) { "Default state must be one of the states" }
    }

    companion object Builder {
        operator fun invoke(builder: @ButtonBuilderDslMarker SwitchButtonDslBuilder.() -> Unit) =
            SwitchButtonDslBuilder().apply(builder).build()
    }

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

@ButtonBuilderDslMarker
class SwitchButtonDslBuilder : ButtonDslBuilder() {
    private val states = mutableObjectListOf<Button>()
    private var defaultState: Button? = null
    private var onStateChange: SwitchButtonStateChangeHandler? = null

    fun state(builder: @ButtonBuilderDslMarker ButtonDslBuilder.() -> Unit) {
        states.add(Button(builder))
    }

    fun state(button: Button) {
        states.add(button)
    }

    fun states(buttons: List<Button>) {
        states.addAll(buttons)
    }

    fun states(amount: Int, builder: @ButtonBuilderDslMarker ButtonDslBuilder.(Int) -> Unit) {
        repeat(amount) { state { builder(it) } }
    }

    fun defaultState(button: Button) {
        defaultState = button
    }

    fun defaultState(builder: @ButtonBuilderDslMarker ButtonDslBuilder.() -> Unit) {
        defaultState = Button(builder)
    }

    fun onStateChange(handler: SwitchButtonStateChangeHandler) {
        onStateChange = handler
    }

    override fun build(): SwitchButton {
        check(states.isNotEmpty()) { "SwitchButton states are empty!" }
        val defaultState = defaultState ?: states.first()

        return SwitchButton(states, defaultState, onStateChange)
    }
}