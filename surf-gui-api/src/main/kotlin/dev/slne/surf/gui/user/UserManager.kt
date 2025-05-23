package dev.slne.surf.gui.user

import dev.slne.surf.gui.api
import dev.slne.surf.gui.common.freeze
import dev.slne.surf.gui.common.mutableObject2ObjectMapOf
import java.util.*

object UserManager {

    private val _users = mutableObject2ObjectMapOf<UUID, User>()
    val users = _users.freeze()

    /**
     * Gets a user from the cache or creates a new one if it doesn't exist.
     */
    operator fun get(uuid: UUID): User {
        return _users.computeIfAbsent(uuid) { uuid: UUID -> api.createNewUser(uuid) }
    }

    /**
     * Removes a user from the cache.
     */
    fun remove(uuid: UUID) {
        _users.remove(uuid)
    }
}