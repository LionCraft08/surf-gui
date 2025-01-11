package dev.slne.packetuxui.menu.page

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import dev.slne.packetuxui.menu.button.Button
import dev.slne.packetuxui.menu.button.ButtonBuilder
import dev.slne.packetuxui.menu.menu.Menu
import dev.slne.packetuxui.menu.menu.MenuType
import dev.slne.packetuxui.menu.menu.menu
import dev.slne.packetuxui.menu.utils.Position
import dev.slne.packetuxui.menu.utils.PositionRange
import net.kyori.adventure.text.Component

data class Page(
    val buttons: List<Button>
) {
    override fun toString(): String {
        return "Page(buttons=$buttons)"
    }
}

open class PaginatedMenu(
    name: Component,
    type: MenuType,
    pageButtons: List<Button>,
    private val buttonRange: PositionRange,
    private val previousPageButtonSlot: Position,
    private val nextPageButtonSlot: Position,

    private val noPageItemStack: ItemStack = ItemStack.EMPTY,
    private val nextPageItemStack: ItemStack = ItemStack.builder().type(ItemTypes.ARROW)
        .component(ComponentTypes.ITEM_NAME, Component.text("Nächste Seite")).build(),
    private val previousPageItemStack: ItemStack = ItemStack.builder().type(ItemTypes.ARROW)
        .component(ComponentTypes.ITEM_NAME, Component.text("Nächste Seite")).build()

) : Menu(name, type, mapOf()) {
    private var currentPage = 0
    private val pages = mutableListOf<Page>()

    init {
        val pageSize = buttonRange.endInclusive.toSlot() - buttonRange.start.toSlot() + 1

        pages.addAll(pageButtons.chunked(pageSize).map { Page(it) })

        updateItems()
    }

    fun setCurrentPageButtons() {
        val page = pages[currentPage]

        buttonRange.forEachIndexed { index, position ->
            if (index < page.buttons.size) {
                buttons[position.toSlot()] = page.buttons[index]
            } else {
                buttons[position.toSlot()] = ButtonBuilder().apply {
                    item(ItemStack.builder().type(ItemTypes.AIR).build())
                }.build()
            }
        }
    }

    fun setPreviousPageButton() {
        buttons[previousPageButtonSlot.toSlot()] = ButtonBuilder().apply {
            item(if (hasPreviousPage()) previousPageItemStack else noPageItemStack)

            click {
                previousPage()
            }
        }.build()
    }

    fun setNextPageButton() {
        buttons[nextPageButtonSlot.toSlot()] = ButtonBuilder().apply {
            item(if (hasNextPage()) nextPageItemStack else noPageItemStack)

            click {
                nextPage()
            }
        }.build()
    }

    private fun updateItems() {
        setCurrentPageButtons()

        setPreviousPageButton()
        setNextPageButton()

        viewers.forEach { (user, _) ->
            updateSlots(user)
        }
    }

    fun nextPage() {
        if (hasNextPage()) {
            currentPage++

            updateItems()
        }
    }

    fun previousPage() {
        if (hasPreviousPage()) {
            currentPage--

            updateItems()
        }
    }

    fun hasNextPage() = currentPage < pages.size - 1
    fun hasPreviousPage() = currentPage > 0

}

fun main() {

    val protectionMembers = listOf("Alice", "Bob", "Charlie")
    val protectionButtons = protectionMembers.map { member ->
        ButtonBuilder().apply {
            item(ItemStack.builder().type(ItemTypes.PLAYER_HEAD).build())
        }.build()
    }

    val menu = menu(MenuType.GENERIC9X5) {
        buildAllButtons { slot ->
            when (slot) {
                in 0..8 -> button(slot) {
                    item(ItemStack.builder().type(ItemTypes.BLACK_STAINED_GLASS_PANE).build())
                }

                in 32..40 -> button(slot) {
                    item(ItemStack.builder().type(ItemTypes.BLACK_STAINED_GLASS_PANE).build())
                }
            }
        }

        button(4) {
            item(
                ItemStack.builder().type(ItemTypes.CHEST)
                    .component(ComponentTypes.ITEM_NAME, Component.text("Test")).build()
            )
        }
    }
}