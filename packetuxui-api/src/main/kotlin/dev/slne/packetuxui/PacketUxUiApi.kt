package dev.slne.packetuxui

import com.github.retrooper.packetevents.PacketEvents
import dev.slne.packetuxui.listeners.PacketListener
import dev.slne.packetuxui.user.User
import java.util.*

internal val api get() = PacketUxUiApi.getInstance()

abstract class PacketUxUiApi {

    private var initialized = false

    abstract fun createNewUser(uuid: UUID): User

    abstract suspend fun getNextContainerId(user: User): Int
    abstract suspend fun hasOpenedContainer(user: User): Boolean

    fun init() {
        if (initialized) return
        val packetEvents = PacketEvents.getAPI()

        if (!packetEvents.isLoaded) {
            packetEvents.load()
        }

        if (!packetEvents.isInitialized) {
            packetEvents.init()
        }

        packetEvents.eventManager.registerListener(PacketListener)

        initialized = true
    }

    fun terminate() {
        if (!initialized) return

        initialized = false
    }

    companion object {
        private var instance: PacketUxUiApi? = null

        fun getInstance(): PacketUxUiApi {
            if (instance == null) {
                throw IllegalStateException("PacketUxUiApi has not been initialized yet.")
            }

            return instance!!
        }

        fun setInstance(api: PacketUxUiApi) {
            instance = api
        }
    }
}


