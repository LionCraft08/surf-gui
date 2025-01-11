package dev.slne.packetuxui.user

import dev.slne.packetuxui.api
import dev.slne.packetuxui.common.freeze
import dev.slne.packetuxui.common.mutableObject2ObjectMapOf
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