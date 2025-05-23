package dev.slne.surf.gui.menu.menu.specific

import dev.slne.surf.gui.common.int2ObjectMapOf
import dev.slne.surf.gui.common.toComponent
import dev.slne.surf.gui.common.toLoreComponents
import dev.slne.surf.gui.menu.item.CustomItemProvider
import dev.slne.surf.gui.menu.menu.DefaultMenu
import dev.slne.surf.gui.menu.menu.Menu
import dev.slne.surf.gui.menu.menu.MenuService
import dev.slne.surf.gui.menu.menu.MenuType
import dev.slne.surf.gui.util.DM
import dev.slne.surf.gui.util.Slot
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent

class MainMenu(): DefaultMenu(Component.text(" "), MenuType.GENERIC9X6, int2ObjectMapOf(),"main_menu") {
    init {
        setButton(Slot(4, 4), CustomItemProvider("cc_animated_bot", false).toButton())
        setButton(Slot(4, 1), CustomItemProvider("cc_animated_top", false).toButton())

        setButton(Slot(1, 2, 9),
            CustomItemProvider(
                "web",
                true,
                Component.text("Links"),
                mutableListOf<Component>(Component.text("Wichtige Links rund um den Server"))).toButton{
                    (user, buttonType, slot, itemStack, menu) ->
                        MenuService.openMenu(user, "book:links")
                    })
        setButton(Slot(1, 3),
            CustomItemProvider("compass",
                true,
                Component.text("Navigator"),
                mutableListOf<Component>(Component.text("Serverwechsel leicht gemacht ^^"))).toButton{(user, buttonType, slot, itemStack, menu) ->
                    MenuService.openMenu(user, "navigator")//TODO move menu 'navigator' into this api?? (Idk where it currently is)

            })
        setButton(Slot(7, 2),
            CustomItemProvider("friends",//TODO Create this icon
            true,
                "<gradient:#0099FF:#8888FF>Freunde".toComponent(),
                "Öffnet ein Menu, um deine Freunde <br>und Anfragen zu verwalten.".toLoreComponents()
            ).toButton { (user, buttonType, slot, itemStack, menu) ->
                MenuService.openMenu(user, "friends:${user.uuid}")
            })
        setButton(Slot(7, 3),
            CustomItemProvider("clan",//TODO Create this icon
                true,
                "<red>Clan".toComponent(),
                "Öffnet ein Menu, um deinen aktuellen <br>Clan zu verwalten".toLoreComponents()
            ).toButton { (user, buttonType, slot, itemStack, menu) ->
                MenuService.openMenu(user, "clan")//TODO Create this menu in Surf Clan
            })//FIXME Maybe move that to the Clan Plugin?
    }
}