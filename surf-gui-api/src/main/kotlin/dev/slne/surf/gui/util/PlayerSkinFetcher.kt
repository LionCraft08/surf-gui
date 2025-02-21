package dev.slne.surf.gui.util

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.google.gson.JsonParser
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.gui.common.toObjectList
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.coroutines.executeAsync
import java.util.*
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.minutes

/**
 * Fetches the skin of a player from Mojang's session server.
 */
object PlayerSkinFetcher {
    private val client = OkHttpClient.Builder().build()
    private val SKIN_CACHE =
        Caffeine.newBuilder()
            .expireAfterWrite(10.minutes)
            .asLoadingCache<UUID, List<TextureProperty>> { fetchSkin0(it) }

    /**
     * Fetches the skin of a player from Mojang's session server.
     *
     * @param playerUuid The UUID of the player to fetch the skin for.
     * @return The skin properties of the player.
     */
    suspend fun fetchSkin(playerUuid: UUID) = SKIN_CACHE.get(playerUuid)

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun fetchSkin0(uuid: UUID): List<TextureProperty> {
        val request = Request.Builder()
            .url("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false")
            .build()

        client.newCall(request).executeAsync().use { response ->
            val responseString = response.body.string()
            val jsonObject = JsonParser.parseString(responseString).getAsJsonObject()
            val properties = jsonObject["properties"].getAsJsonArray()
            return properties.asSequence()
                .map { it.getAsJsonObject() }
                .map {
                    TextureProperty(
                        it["name"].asString,
                        it["value"].asString,
                        it["signature"]?.asString
                    )
                }.toObjectList()
        }
    }
}

/**
 * The scope used for fetching player skins.
 */
internal object SkinFetcherScope : CoroutineScope {
    val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    override val coroutineContext = dispatcher + CoroutineName("SkinFetcher")
}