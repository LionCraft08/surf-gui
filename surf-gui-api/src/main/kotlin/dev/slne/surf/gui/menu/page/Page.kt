package dev.slne.surf.gui.menu.page

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.menu.Menu
import dev.slne.surf.gui.menu.menu.MenuType
import dev.slne.surf.gui.menu.utils.Position
import dev.slne.surf.gui.menu.utils.PositionRange
import net.kyori.adventure.text.Component

/**
 * Represents a page in a paginated menu.
 *
 * @property buttons The buttons on this page.
 */
data class Page(
    val buttons: List<Button>
) {
    override fun toString(): String {
        return "Page(buttons=$buttons)"
    }
}

/**
 * Represents a paginated menu.
 *
 * @property name The name of the menu.
 * @property type The type of the menu.
 * @property pageButtons The buttons to paginate.
 * @property buttonRange The range of slots to place the buttons in.
 * @property previousPageButtonSlot The slot of the previous page button.
 * @property nextPageButtonSlot The slot of the next page button.
 *
 * @property noPageItemStack The item stack to display when there are no pages.
 * @property nextPageItemStack The item stack to display for the next page button.
 * @property previousPageItemStack The item stack to display for the previous page button.
 */
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

    /**
     * Updates the buttons on the current page.
     */
    fun setCurrentPageButtons() {
        val page = pages[currentPage]

        buttonRange.forEachIndexed { index, position ->
            if (index < page.buttons.size) {
                buttons[position.toSlot()] = page.buttons[index]
            } else {
                buttons[position.toSlot()] = dev.slne.surf.gui.menu.button.ButtonBuilder().apply {
                    item(ItemStack.builder().type(ItemTypes.AIR).build())
                }.build()
            }
        }
    }

    /**
     * Sets the previous page button.
     */
    fun setPreviousPageButton() {
        buttons[previousPageButtonSlot.toSlot()] = dev.slne.surf.gui.menu.button.ButtonBuilder()
            .apply {
                item(if (hasPreviousPage()) previousPageItemStack else noPageItemStack)

                click {
                    previousPage()
                }
            }.build()
    }

    /**
     * Sets the next page button.
     */
    fun setNextPageButton() {
        buttons[nextPageButtonSlot.toSlot()] = dev.slne.surf.gui.menu.button.ButtonBuilder().apply {
            item(if (hasNextPage()) nextPageItemStack else noPageItemStack)

            click {
                nextPage()
            }
        }.build()
    }

    /**
     * Updates the items in the menu.
     */
    private fun updateItems() {
        setCurrentPageButtons()

        setPreviousPageButton()
        setNextPageButton()

        viewers.forEach { (user, _) ->
            updateSlots(user)
        }
    }

    /**
     * Moves to the next page.
     */
    fun nextPage() {
        if (hasNextPage()) {
            currentPage++

            updateItems()
        }
    }

    /**
     * Moves to the previous page.
     */
    fun previousPage() {
        if (hasPreviousPage()) {
            currentPage--

            updateItems()
        }
    }

    /**
     * Checks if there is a next page.
     *
     * @return `true` if there is a next page, `false` otherwise.
     */
    fun hasNextPage() = currentPage < pages.size - 1

    /**
     * Checks if there is a previous page.
     *
     * @return `true` if there is a previous page, `false` otherwise.
     */
    fun hasPreviousPage() = currentPage > 0

}