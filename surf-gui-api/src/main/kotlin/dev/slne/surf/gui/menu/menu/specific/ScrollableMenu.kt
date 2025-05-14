package dev.slne.surf.gui.menu.menu.specific

import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import dev.slne.surf.gui.common.int2ObjectMapOf
import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.button.click.ExecutableComponent
import dev.slne.surf.gui.menu.item.CustomItemProvider
import dev.slne.surf.gui.menu.item.CustomScrollBarProvider
import dev.slne.surf.gui.menu.item.ItemBuilder
import dev.slne.surf.gui.menu.menu.DefaultMenu
import dev.slne.surf.gui.menu.menu.MenuType
import dev.slne.surf.gui.user.User
import dev.slne.surf.gui.util.Slot
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import net.kyori.adventure.text.Component

open class ScrollableMenu(
    name: Component,
    val allButtons: Int2ObjectMap<Button>,
    var additionalButton: Button? = null
): DefaultMenu(name, MenuType.GENERIC9X6, int2ObjectMapOf(), "scrollable") {
    var page = 0

    init {
        update()
    }

    protected fun update(){
        var x = 0
        var y = 1
        for (i in IntRange(page*32, page*32+31)){
            if (i<getAmountOfButtons()){
                super.setButton(Slot(x, y), getButton(i))
            } else {
                super.setButton(Slot(x, y), null)
            }

            x++
            //Ensure the right side stays empty
            if ((x+1)%9==0){x=0;y++}
        }

        super.setButton(Slot(8, 1), CustomItemProvider(
            if (hasPreviousPage()) "up" else "up_gray",
            false
        ).toButton { (user, _, _, _, _) -> scrollUp(user) })
        super.setButton(Slot(8, 4), CustomItemProvider(
            if (hasNextPage()) "down" else "down_gray",
            false
        ).toButton { (user, _, _, _, _) -> scrollDown(user) })
        super.setButton(Slot(8, 3), CustomScrollBarProvider(getPages(), page).toButton())


    }
    fun getButton(index:Int): Button{
        return if (allButtons.size > index) allButtons.get(index)!!
        else if (allButtons.size == index&& additionalButton!= null) additionalButton!!
        else Button(ItemBuilder(ItemTypes.GRAY_STAINED_GLASS_PANE) { name(Component.text("Error in Code")) })
    }
    fun hasPreviousPage(): Boolean{
        return page > 0
    }
    fun addButton(button: Button){
        allButtons.put(allButtons.size, button)
        update()
    }
    fun scrollDown(user: User){
        if (hasNextPage()){
            page++
            update()
            sendWindowItems(user)
        }
    }
    fun scrollUp(user: User){
        if (hasPreviousPage()){
            page--
            update()
            sendWindowItems(user)
        }
    }
    fun hasNextPage(): Boolean{
        return (page+1)*32<getAmountOfButtons()
    }
    fun getAmountOfButtons():Int{
        return allButtons.size + if (additionalButton != null) 1 else 0
    }
    fun getPages():Int{
        return getAmountOfButtons()/32+if (getAmountOfButtons()%32==0) 0 else 1
    }
}