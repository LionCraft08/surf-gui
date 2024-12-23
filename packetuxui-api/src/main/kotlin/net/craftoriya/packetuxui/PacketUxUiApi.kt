package net.craftoriya.packetuxui

import com.github.retrooper.packetevents.PacketEvents
import net.craftoriya.packetuxui.controller.PacketListener
import net.craftoriya.packetuxui.user.User

abstract class PacketUxUiApi {

    private var initialized = false

    abstract suspend fun getNextContainerId(user: User): Int

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
}


