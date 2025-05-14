package dev.slne.surf.gui.menu.item

import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import dev.slne.surf.gui.common.mutableObjectListOf
import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.button.click.ExecutableComponent
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

open class CustomItemProvider (modelData:String?, visibleOnHover:Boolean? = true, name:Component? = null, lore: MutableList<Component>? = null) : ItemBuilder() {
    init {
        super.itemType = ItemTypes.PAPER
        super.amount = 1
        super.modelData = modelData
        super.name = name
        if (visibleOnHover != null) visibleHover = visibleOnHover
        if (lore != null) super.lore(lore)
    }
    fun toButton(): Button{
        return Button(build())
    }
    fun toButton(onExecute: ExecutableComponent): Button{
        return Button(build(), onExecute)
    }
    companion object{
        fun getConfirmButton(): ItemStack{
            return CustomItemProvider("check", true, MiniMessage.miniMessage().deserialize("<green>Bestätigen")).build()
        }
        fun getDeclineButton(): ItemStack{
            return CustomItemProvider("decline", true, MiniMessage.miniMessage().deserialize("<red>Abbrechen")).build()
        }
    }
}