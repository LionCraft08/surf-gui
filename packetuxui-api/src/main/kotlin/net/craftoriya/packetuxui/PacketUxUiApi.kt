package net.craftoriya.packetuxui

import com.github.retrooper.packetevents.PacketEvents
import net.craftoriya.packetuxui.controller.PacketListener

object PacketUxUiApi {

    private var initialized = false

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


