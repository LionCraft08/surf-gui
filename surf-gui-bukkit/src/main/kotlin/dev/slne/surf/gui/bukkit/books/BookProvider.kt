package dev.slne.surf.gui.bukkit.books

import dev.slne.surf.gui.common.mutableObject2ObjectMapOf
import dev.slne.surf.gui.common.mutableObjectListOf
import dev.slne.surf.gui.menu.menu.DefaultMenu
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import java.security.InvalidParameterException

object BookProvider {
    private val books = mutableObject2ObjectMapOf<String, Book>()
    fun open(book: String, player: Player){
        if (books.contains(book)){
            player.openBook(books.get(book)!!)
        }else throw InvalidParameterException("Book $book was not found")
    }

    init {
        books.put("links",
            Book.book(
                Component.text("Links"),
                Component.text("LionK08"),
                MiniMessage.miniMessage().deserialize("<gradient:#9900ff:blue>Castcrafter Server Links</gradient><br> <br><#00ffff><gradient:#00ffff:#0066ff>|-> <click:open_url:'https://server.castcrafter.de/community-server-landing-page.html'>Community Server Docs</click><br>|<br><click:open_url:'https://discord.gg/castcrafter'>|-> Discord</click><br>|<br>|-> <click:open_url:'https://www.youtube.com/@CastCrafter'>YouTube</click><br>|<br>|-> <click:open_url:'https://discord.com/channels/133198459531558912/1124438644523012234'>Ticket Erstellen</click>"),
            ))
    }

}