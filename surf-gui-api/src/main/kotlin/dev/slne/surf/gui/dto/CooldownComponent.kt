package dev.slne.surf.gui.dto

import dev.slne.surf.gui.menu.button.click.ExecutableComponent
import kotlin.math.max

/**
 * CooldownComponent is a data class that represents a cooldown for a specific action.
 *
 * @property delay The delay in milliseconds before the action can be executed again.
 * @property freeze The delay in milliseconds before the action can be executed again after a freeze.
 * @property execute The action to be executed when the cooldown expires.
 */
data class CooldownComponent(
    val delay: Long = 0,
    val freeze: Long = 0,
    val execute: ExecutableComponent? = null,
) {
    companion object {
        val EMPTY = CooldownComponent()
    }

    private var expireTime: Long = 0
    private var expireFreeze: Long = 0

    /**
     * Combines this cooldown with another cooldown.
     *
     * @param cooldown The cooldown to combine with.
     * @return The combined cooldown.
     */
    fun combine(cooldown: CooldownComponent): CooldownComponent {
        val execute =
            if (this.execute != null && cooldown.execute != null) {
                if (this.delay >= cooldown.delay) this.execute else cooldown.execute
            } else this.execute ?: cooldown.execute

        val combined = CooldownComponent(
            delay = max(this.delay, cooldown.delay),
            execute = execute,
            freeze = max(this.freeze, cooldown.freeze)
        )

        combined.expireTime = max(this.expireTime, cooldown.expireTime)
        combined.expireFreeze = max(this.expireFreeze, cooldown.expireFreeze)

        return combined
    }

    /**
     * Resets the freeze time.
     */
    fun resetFreeze() {
        expireFreeze = System.currentTimeMillis() + freeze
    }

    /**
     * Resets the expire time.
     */
    fun resetExpire() {
        expireTime = System.currentTimeMillis() + delay
    }

    /**
     * Checks if the freeze time has expired.
     */
    fun isFreezeExpired(now: Long): Boolean = now >= expireFreeze

    /**
     * Checks if the expire time has expired.
     */
    fun isTimeExpired(now: Long): Boolean = now >= expireTime
}