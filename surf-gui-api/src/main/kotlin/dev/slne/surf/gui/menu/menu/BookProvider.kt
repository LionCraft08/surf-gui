package dev.slne.surf.gui.menu.menu

import dev.slne.surf.gui.SurfGuiApi
import dev.slne.surf.gui.common.mutableObject2ObjectMapOf
import dev.slne.surf.gui.common.toComponent
import dev.slne.surf.gui.common.toPlain
import dev.slne.surf.gui.communication.CommunicationHandler
import dev.slne.surf.gui.user.User
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import java.security.InvalidParameterException

object BookProvider {
    private val books = mutableObject2ObjectMapOf<String, Book>()
    fun open(book: String, player: User){
        if (SurfGuiApi.isBackend()) {
            if (books.contains(book)) {
                player.openBook(books.get(book)!!)
            } else {
                player.sendMessage("<gradient:blue:#00aa00>|Surf -> Das angefragte Buch '$book' konnte nicht gefunden werden.".toComponent())
                throw InvalidParameterException("Book $book was not found")
            }
        }else CommunicationHandler.sendInventoryRequest(player.uuid, "book:${book}")
    }

    init {
        books.put("links",
            Book.book(
                Component.text("Links"),
                Component.text("LionK08"),
                MiniMessage.miniMessage().deserialize("<gradient:#9900ff:blue>Castcrafter Server Links</gradient><br> <br><#00ffff><gradient:#00ffff:#0066ff>|-> <click:open_url:'https://server.castcrafter.de/community-server-landing-page.html'>Community Server Docs</click><br>|<br><click:open_url:'https://discord.gg/castcrafter'>|-> Discord</click><br>|<br>|-> <click:open_url:'https://www.youtube.com/@CastCrafter'>YouTube</click><br>|<br>|-> <click:open_url:'https://discord.com/channels/133198459531558912/1124438644523012234'>Ticket Erstellen</click>"),
            ))
    }
    fun getBook(book: String): Book? = books.get(book)
    fun getBooks(): MutableMap<String, Book> = books
    fun getBookNames(): MutableSet<String> = books.keys
    fun containsBook(book: String): Boolean = books.contains(book)
    fun removeBook(book: String): Boolean = books.remove(book) != null
    fun clearBooks() = books.clear()
    fun size(): Int = books.size
    fun isEmpty(): Boolean = books.isEmpty()
    fun isNotEmpty(): Boolean = books.isNotEmpty()
    fun register(book: Book){
        if (!SurfGuiApi.isBackend()) throw InvalidParameterException("Cannot register books on Frontend")
        books.put(book.title().toPlain(), book)
    }

}