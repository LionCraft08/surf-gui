package dev.slne.surf.gui.menu.item

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemCustomModelData
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import dev.slne.surf.gui.util.PlayerSkinFetcher
import dev.slne.surf.gui.util.SkinFetcherScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * A builder for creating a player head item.
 *
 * @see ItemBuilder
 */
class HeadItemBuilder : ItemBuilder() {
    var base64: String = ""

    /**
     * Fetches the head texture for the given name.
     *
     * @param name The name of the player to fetch the head texture for.
     * @return This builder.
     */
    fun headTextureFromName(name: String) = apply {
        println("Fetching head texture for name: $name")
    }

    /**
     * Fetches the head texture from the given URL.
     *
     * @param url The URL to fetch the head texture from.
     * @return This builder.
     */
    fun headTextureFromUrl(url: String) = apply {
        println("Fetching head texture from URL: $url")
    }

    /**
     * Fetches the head texture for the given UUID.
     *
     * @param uuid The UUID of the player to fetch the head texture for.
     * @return This builder.
     */
    fun headTextureFromUuid(uuid: UUID) = apply {
        println("Fetching head texture for UUID: $uuid")
        SkinFetcherScope.launch {
            val properties = PlayerSkinFetcher.fetchSkin(uuid)
            val texture = properties.firstOrNull { it.name == "textures" }
            if (texture != null) {
                base64 = texture.value
            } else {
                println("Failed to fetch head texture for UUID: $uuid")
            }
        }
    }

    /**
     * Builds the item stack.
     *
     * @return The item stack.
     */
    override fun build(): ItemStack {
        val item = ItemStack.builder()
            .type(itemType)
            .component(ComponentTypes.LORE, ItemLore(lore))
            .amount(amount)
            .component(
                ComponentTypes.ENCHANTMENTS,
                ItemEnchantments(enchantments, enchantVisibility)
            )
        modelData?.let { item.component(ComponentTypes.CUSTOM_MODEL_DATA_LISTS, ItemCustomModelData(null, null, listOf(it), null)) }
        name?.let { item.component(ComponentTypes.ITEM_NAME, it) }

        if (itemType == ItemTypes.PLAYER_HEAD) {
            item.component(
                ComponentTypes.PROFILE,
                ItemProfile(null, null, listOf(ItemProfile.Property("textures", base64, null)))
            )
        }
        return item.build()
    }
}