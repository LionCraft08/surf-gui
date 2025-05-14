package dev.slne.surf.gui.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor


object DM {
    val messagePrefix: Component = getRawMessagePrefix()
    var wrongArgs: Component = messagePrefix.append(Component.text("Wrong usage of args.", TextColor.color(255, 0, 0)))
    var noPlayer: Component =
        messagePrefix.append(Component.text("Could not find this Player.", TextColor.color(255, 0, 0)))
    var commandError: Component = messagePrefix.append(
        Component.text(
            "An error occurred when trying to execute this command.",
            TextColor.color(255, 0, 0)
        )
    )
    var noPermission: Component =
        messagePrefix.append(Component.text("You do not have the permission to do this.", TextColor.color(255, 0, 0)))
    var wait: Component =
        messagePrefix.append(Component.text("Please wait a bit before doing that.", TextColor.color(255, 0, 0)))
    var notAPlayer: Component = messagePrefix.append(
        Component.text(
            "This Command can only be executed as a Player!",
            TextColor.color(255, 0, 0)
        )
    )

    /**Creates an Error Message with the LionSystems Prefix and
     * the [TextColor] Orange if none is previously set.
     * @param message the raw Message
     * @return the Styled Message
     */
    fun error(message: Component): Component {
        var message = message
        message = message.colorIfAbsent(TextColor.color(TextColor.color(255, 128, 0)))
        message = messagePrefix.append(message)
        return message
    }

    /**Creates an Error Message with the LionSystems Prefix and
     * the [TextColor] Orange
     * @param message the raw Message
     * @return the Styled Message
     */
    fun error(message: String): Component {
        return error(Component.text(message))
    }

    /**Creates an Error Message with the LionSystems Prefix and
     * the [TextColor] Red if none is previously set.
     * @param message the raw Message
     * @return the Styled Message
     */
    fun fatalError(message: Component): Component {
        var message = message
        message = message.colorIfAbsent(TextColor.color(TextColor.color(255, 0, 0)))
        message = messagePrefix.append(message)
        return message
    }

    /**Creates an Error Message with the LionSystems Prefix and
     * the [TextColor] Red
     * @param message the raw Message
     * @return the Styled Message
     */
    fun fatalError(message: String): Component {
        return fatalError(Component.text(message))
    }

    /**Creates a Message with the LionSystems Prefix
     * @param message the raw Message
     * @return the Styled Message
     */
    fun info(message: Component): Component {
        return messagePrefix.append(message)
    }

    /**Creates a Message with the LionSystems Prefix
     * @param message the raw Message
     * @return the Styled Message
     */
    fun info(message: String): Component {
        return info(Component.text(message))
    }

    /**Creates a Message with the LionSystems Prefix
     * @param seconds The time to wait in Seconds
     * @return the Styled Message
     */
    fun waitSeconds(seconds: Int?): Component {
        if (seconds == null) {
            return wait
        } else {
            if (seconds >= 60) {
                return messagePrefix.append(
                    Component.text(
                        "Please try again in " + seconds / 60 + " Seconds!",
                        TextColor.color(255, 128, 0)
                    )
                )
            }
            return messagePrefix.append(
                Component.text(
                    "Please try again in " + seconds + " Seconds!",
                    TextColor.color(255, 128, 0)
                )
            )
        }
    }

    /**Creates a Message with the LionSystems Prefix and converts the Ticks into Seconds
     * @param ticks The time to wait in Ticks
     * @return the Styled Message
     */
    fun waitTicks(ticks: Int?): Component {
        var ticks = ticks
        if (ticks == null) {
            return wait
        } else {
            if (ticks < 20) ticks = 20
            return waitSeconds(ticks / 20)
        }
    }

    private fun getRawMessagePrefix(): Component {
        return Component.text("<", TextColor.color(255, 255, 255))
            .append(Component.text("L", TextColor.color(255, 0, 255)))
            .append(Component.text("i", TextColor.color(220, 0, 255)))
            .append(Component.text("o", TextColor.color(190, 0, 255)))
            .append(Component.text("n", TextColor.color(160, 0, 255)))
            .append(Component.text("S", TextColor.color(130, 0, 255)))
            .append(Component.text("y", TextColor.color(100, 0, 255)))
            .append(Component.text("s", TextColor.color(70, 0, 255)))
            .append(Component.text("t", TextColor.color(40, 0, 255)))
            .append(Component.text("e", TextColor.color(10, 0, 255)))
            .append(Component.text("m", TextColor.color(0, 20, 255)))
            .append(Component.text("s", TextColor.color(0, 50, 255)))
            .append(Component.text("> ", TextColor.color(255, 255, 255)))
    }
}
