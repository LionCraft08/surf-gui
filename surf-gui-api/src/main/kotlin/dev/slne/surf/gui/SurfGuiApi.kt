package dev.slne.surf.gui

import com.github.retrooper.packetevents.PacketEvents
import dev.slne.surf.gui.listeners.PacketListener
import dev.slne.surf.gui.user.User
import java.util.*

internal val api get() = SurfGuiApi.getInstance()

/**
 * The main API class for the Surf GUI API.
 */
abstract class SurfGuiApi {

    private var initialized = false

    /**
     * Creates a new user object.
     *
     * @param uuid The UUID of the user.
     * @return The new user object.
     */
    abstract fun createNewUser(uuid: UUID): User

    /**
     * Gets the next container ID for the user.
     *
     * @param user The user.
     * @return The next container ID.
     */
    abstract suspend fun getNextContainerId(user: User): Int

    /**
     * Checks if the user has an opened container.
     *
     * @param user The user.
     * @return True if the user has an opened container, false otherwise.
     */
    abstract suspend fun hasOpenedContainer(user: User): Boolean

    /**
     * Initializes the API.
     */
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

    /**
     * Terminates the API.
     */
    fun terminate() {
        if (!initialized) return

        initialized = false
    }

    companion object {
        private var instance: SurfGuiApi? = null

        /**
         * Gets the instance of the API.
         *
         * @return The instance of the API.
         */
        fun getInstance(): SurfGuiApi {
            if (instance == null) {
                throw IllegalStateException("SurfGuiApi has not been initialized yet.")
            }

            return instance!!
        }

        /**
         * Sets the instance of the API.
         *
         * @param api The instance of the API.
         */
        fun setInstance(api: SurfGuiApi) {
            instance = api
        }
    }
}


