package dev.slne.surf.gui.menu.item

import dev.slne.surf.gui.SurfGuiApi
import kotlin.math.roundToInt

class CustomScrollBarProvider(
    pages: Int,
    selectedPage:Int
): CustomItemProvider(
getName(pages, selectedPage),
    true,
) {

}
fun getName(pages: Int,selectedPage:Int): String{
    val slices = if (pages==0) 1
        else if (pages<=4) pages
        else if (pages<=8) 8
        else 16
    val position = if (pages<=4) selectedPage
    else if (pages!=selectedPage-1) (slices.toFloat() / pages.toFloat() * selectedPage.toFloat()).roundToInt()
    else slices-1
    SurfGuiApi.getInstance().debug("slices: $slices, position: $position")
    return "scroll_bar_${slices}_$position"
}