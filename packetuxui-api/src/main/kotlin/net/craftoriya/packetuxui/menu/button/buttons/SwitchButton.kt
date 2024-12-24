package net.craftoriya.packetuxui.menu.button.buttons

import net.craftoriya.packetuxui.common.mutableObjectListOf
import net.craftoriya.packetuxui.menu.button.Button
import net.craftoriya.packetuxui.menu.button.ButtonBuilderDslMarker
import net.craftoriya.packetuxui.menu.button.ButtonDslBuilder
import net.craftoriya.packetuxui.menu.button.click.ExecuteComponent

typealias SwitchButtonStateChangeHandler = (fromState: Button, toState: Button) -> Unit

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

fun SwitchButton(builder: @ButtonBuilderDslMarker SwitchButtonDslBuilder.() -> Unit): SwitchButton {
    return SwitchButtonDslBuilder().apply(builder).build()
}