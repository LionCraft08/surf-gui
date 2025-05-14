package dev.slne.surf.gui.velocity

import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import dev.slne.surf.gui.SurfGuiApi
import dev.slne.surf.gui.communication.CommunicationHandler
import dev.slne.surf.gui.user.User
import dev.slne.surf.gui.velocity.user.VelocityUser
import java.io.File
import java.util.UUID

object SurfGUIVelocityAPI: SurfGuiApi() {

    init {
        setInstance(this, false)
    }

    override fun createNewUser(uuid: UUID): User {
        return VelocityUser(uuid)
    }


    override suspend fun sendMenuMessage(playername: String, channel: String, data: ByteArray) {
        val p = SurfGuiVelocityPlugin.getInstance().server.getPlayer(UUID.fromString(playername))?.get()
        if (p == null) throw NullPointerException("Could not find the Player named $playername")
        p.currentServer.get().sendPluginMessage(identifier, data)
    }

    override fun getDataFile(): File {
        return SurfGuiVelocityPlugin.getInstance().dataDirectory.toFile()
    }

    override fun log(message: String, level: System.Logger.Level) {
        SurfGuiVelocityPlugin.getInstance().logger.info(message)
        //TODO Manage The Logger Level
    }

    val identifier: MinecraftChannelIdentifier = MinecraftChannelIdentifier.from(CommunicationHandler.getChannelOpen())
}