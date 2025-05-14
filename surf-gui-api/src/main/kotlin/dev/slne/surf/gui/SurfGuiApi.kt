package dev.slne.surf.gui

import com.github.retrooper.packetevents.PacketEvents
import dev.slne.surf.gui.listeners.PacketListener
import dev.slne.surf.gui.menu.menu.MenuService
import dev.slne.surf.gui.menu.menu.specific.MainMenu
import dev.slne.surf.gui.menu.menu.specific.TestMenu
import dev.slne.surf.gui.user.User
import org.slf4j.Logger
import java.io.File
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

    abstract suspend fun sendMenuMessage(uuid: String, channel: String, data: ByteArray)

    abstract fun getDataFile(): File

    abstract fun log(message: String, level: System.Logger.Level = System.Logger.Level.INFO)

    fun debug(message: String){
        log(message, System.Logger.Level.DEBUG)
    }

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

        MenuService.registerNewMenu("main_menu", MainMenu::class.java)
        MenuService.registerNewMenu("test_menu", TestMenu::class.java)


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
        private var backend: Boolean? = null;

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

        fun isBackend(): Boolean{
            if (backend == null) throw IllegalStateException("SurfGuiApi has not been initialized yet.")
            return backend!!
        }

        /**
         * Sets the instance of the API.
         *
         * @param api The instance of the API.
         */
        fun setInstance(api: SurfGuiApi, backend: Boolean) {
            instance = api
            Companion.backend = backend;
        }
    }
}


