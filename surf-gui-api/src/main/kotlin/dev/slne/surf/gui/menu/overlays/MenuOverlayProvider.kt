package dev.slne.surf.gui.menu.overlays

import com.google.gson.Gson
import dev.slne.surf.gui.SurfGuiApi
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.io.File
import java.util.Scanner


object MenuOverlayProvider {
    private val overlays = HashMap<String, Overlay>()

    fun getOverlay(id:String): Overlay? = overlays[id]

    fun getOverlayString(id: String?):String = overlays[id]?.toString()?:""
    fun getOverlayComponent(id: String?): Component = overlays[id]?.toComponent()?: Component.text("")

    init {
        //val input = Thread.currentThread().contextClassLoader.getResourceAsStream("overlays.json")
        var f = File(SurfGuiApi.getInstance().getDataFile(), "overlays.json")
        val input = if(f.exists()) f.inputStream() else null

        if (input == null) SurfGuiApi.getInstance().log("Could not find any Overlays to load.", System.Logger.Level.WARNING)


        //Could be replaced with external saved json file

        val json = input?.let { Scanner(it).useDelimiter("\\A").next() }
        val array = Gson().fromJson(json, Array<Overlay>::class.java)
        if (array != null){
            for (overlay in array){
                overlays[overlay.name] = overlay
            }
        }

    }
}


data class Overlay(
    val name:String,
    val shift: Int,
    val code:String,
    val textShift:Int
){
    override fun toString():String{
        return NegativeSpaceProvider.getSpacing(shift) + code + NegativeSpaceProvider.getSpacing(textShift)
    }
    fun toComponent(): Component{
        return Component.text(toString(), NamedTextColor.WHITE).append(Component.text("", NamedTextColor.BLACK))
    }
}