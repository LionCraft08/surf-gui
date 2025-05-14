package dev.slne.surf.gui.menu.item

import com.github.retrooper.packetevents.protocol.color.Color
import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemCustomModelData
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentType
import com.github.retrooper.packetevents.protocol.item.type.ItemType
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.util.Dummy
import dev.slne.surf.gui.common.mutableObject2IntMapOf
import dev.slne.surf.gui.common.mutableObjectListOf
import dev.slne.surf.gui.common.toObjectArrayList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class ItemBuilderDslMarker

/**
 * A builder for creating ItemStacks.
 */
@ItemBuilderDslMarker
open class ItemBuilder {
    var itemType: ItemType = ItemTypes.AIR
    var name: Component? = null
    var lore = mutableObjectListOf<Component>()
    var amount = 1
    var enchantments = mutableObject2IntMapOf<EnchantmentType>()
    var enchantVisibility = true
    var modelData: String? = null
    var visibleHover = true

    /**
     * Sets the item type of the ItemStack.
     *
     * @param itemType The item type.
     * @return This ItemBuilder.
     */
    fun itemType(itemType: ItemType) = apply { this.itemType = itemType }

    fun visibleHover(visible:Boolean) = apply { visibleHover = visible }

    /**
     * Sets the name of the ItemStack.
     *
     * @param name The name.
     * @return This ItemBuilder.
     */
    fun name(name: Component) = apply { this.name = name }

    /**
     * Sets the lore of the ItemStack.
     *
     * @param lore The lore.
     * @return This ItemBuilder.
     */
    fun lore(lore: MutableList<Component>) = apply {
        this.lore = lore.map {
            it.decorationIfAbsent(
                TextDecoration.ITALIC,
                TextDecoration.State.FALSE
            )
        }.toObjectArrayList()
    }

    /**
     * Sets the lore of the ItemStack.
     *
     * @param lore The lore.
     * @return This ItemBuilder.
     */
    fun lore(lore: Component) = apply {
        this.lore = mutableObjectListOf(
            lore.decorationIfAbsent(
                TextDecoration.ITALIC,
                TextDecoration.State.FALSE
            )
        )
    }

    /**
     * Sets the lore of the ItemStack.
     *
     * @param lore The lore.
     * @return This ItemBuilder.
     */
    fun lore(vararg lore: Component) = apply {
        this.lore = lore.map {
            it.decorationIfAbsent(
                TextDecoration.ITALIC,
                TextDecoration.State.FALSE
            )
        }.toObjectArrayList()
    }

    /**
     * Sets the amount of the ItemStack.
     *
     * @param amount The amount.
     * @return This ItemBuilder.
     */
    fun amount(amount: Int) = apply { this.amount = amount }

    /**
     * Sets the enchantments of the ItemStack.
     *
     * @param enchantments The enchantments.
     * @param visible Whether the enchantments are visible.
     * @return This ItemBuilder.
     */
    fun enchantments(
        enchantments: MutableMap<EnchantmentType, Int>,
        visible: Boolean = true
    ) = apply {
        this.enchantments += enchantments
        this.enchantVisibility = visible
    }

    /**
     * Sets the enchantments of the ItemStack.
     *
     * @param enchantments The enchantments.
     * @param visible Whether the enchantments are visible.
     * @return This ItemBuilder.
     */
    fun enchantments(
        vararg enchantments: Pair<EnchantmentType, Int>,
        visible: Boolean = true
    ) = apply {
        this.enchantments.clear()
        this.enchantments += enchantments
        this.enchantVisibility = visible
    }

    /**
     * Sets an enchantment of the ItemStack.
     *
     * @param enchantment The enchantment.
     * @param level The level.
     * @param visible Whether the enchantment is visible.
     * @return This ItemBuilder.
     */
    fun enchantment(
        enchantment: EnchantmentType,
        level: Int,
        visible: Boolean = true
    ) = apply {
        this.enchantments[enchantment] = level
        this.enchantVisibility = visible
    }

    /**
     * Sets the custom model data of the ItemStack.
     *
     * @param cmd The custom model data.
     * @return This ItemBuilder.
     */
    fun customModelData(cmd: String) = apply { this.modelData = cmd }

    /**
     * Builds the ItemStack.
     *
     * @return The ItemStack.
     */
    open fun build(): ItemStack {
        val item = ItemStack.builder()
            .type(itemType)
            .component(ComponentTypes.LORE, ItemLore(lore))
            .amount(amount)
            .component(
                ComponentTypes.ENCHANTMENTS,
                ItemEnchantments(enchantments, enchantVisibility)
            )
        modelData?.let { item.component(ComponentTypes.CUSTOM_MODEL_DATA_LISTS, ItemCustomModelData(mutableListOf<Float>(), mutableListOf<Boolean>(), listOf(it), mutableListOf<Color>()))}
        name?.let { item.component(ComponentTypes.ITEM_NAME, it) }
        if (!visibleHover) item.component(ComponentTypes.HIDE_TOOLTIP, Dummy.dummy())
        return item.build()
    }
}

fun ItemBuilder(itemType: ItemType, builder: ItemBuilder.() -> Unit): ItemStack =
    ItemBuilder().apply {
        itemType(itemType)
        builder()
    }.build()