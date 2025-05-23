package dev.slne.surf.gui.menu.page

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import dev.slne.surf.gui.common.int2ObjectMapOf
import dev.slne.surf.gui.common.mutableObjectListOf
import dev.slne.surf.gui.common.toObjectList
import dev.slne.surf.gui.menu.button.Button
import dev.slne.surf.gui.menu.button.ButtonBuilderDslMarker
import dev.slne.surf.gui.menu.button.ButtonDslBuilder
import dev.slne.surf.gui.menu.item.CustomItemProvider
import dev.slne.surf.gui.menu.item.ItemBuilder
import dev.slne.surf.gui.menu.item.ItemBuilderDslMarker
import dev.slne.surf.gui.menu.menu.Menu
import dev.slne.surf.gui.menu.menu.MenuBuilderDsl
import dev.slne.surf.gui.menu.menu.MenuBuilderDslMarker
import dev.slne.surf.gui.menu.menu.MenuType
import dev.slne.surf.gui.util.Slot
import dev.slne.surf.gui.util.SlotRange
import dev.slne.surf.gui.util.slot
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import kotlin.math.max

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

open class PaginatedMenu(
    name: Component,
    type: MenuType,
    pageButtons: List<Button>,
    private val buttonRange: SlotRange,
    private val previousPageButtonSlot: Slot,
    private val nextPageButtonSlot: Slot,

    private val noPageItemStack: ItemStack = ItemStack.EMPTY,
    private val nextPageItemStack: ItemStack = CustomItemProvider("next").build(),
    private val previousPageItemStack: ItemStack = CustomItemProvider("previous").build()
) : Menu(name, type, int2ObjectMapOf()) {
    private var currentPage = 0
    private val pages: ObjectList<Page>

    init {
        val pageSize = buttonRange.size()
        pages = pageButtons.chunked(pageSize) { Page(it) }.toObjectList()

        updateItems()
    }

    fun setCurrentPageButtons() {
        val page = pages[currentPage] ?: error("Page not found")

        buttonRange.forEachIndexed { index, position ->
            if (index < page.buttons.size) {
                buttons[position.toSlot()] = page.buttons[index]
            } else {
                buttons[position.toSlot()] = Button { item(ItemStack.EMPTY) }
            }
        }
    }

    fun setPreviousPageButton() {
        buttons[previousPageButtonSlot.toSlot()] = Button {
            item(if (hasPreviousPage()) previousPageItemStack else noPageItemStack)
            onClick { previousPage() }
        }
    }

    fun setNextPageButton() {
        buttons[nextPageButtonSlot.toSlot()] = Button {
            item(if (hasNextPage()) nextPageItemStack else noPageItemStack)
            onClick { nextPage() }
        }
    }

    private fun updateItems() {
        setCurrentPageButtons()

        setPreviousPageButton()
        setNextPageButton()

        viewers.forEach { user ->
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

/**
 * DSL for creating a paginated menu.
 *
 * @param type The [MenuType] defining the size and layout of the menu.
 * @param builder A lambda to configure the paginated menu.
 * @return A fully configured [PaginatedMenu].
 */
inline fun paginatedMenu(
    type: MenuType,
    builder: @MenuBuilderDslMarker PaginatedMenuDslBuilder.() -> Unit
): PaginatedMenu {
    return PaginatedMenuDslBuilder(type).apply(builder).build()
}

/**
 * Builder class for creating a paginated menu using a DSL.
 *
 * @property type The [MenuType] defining the size and layout of the menu.
 */
@MenuBuilderDslMarker
open class PaginatedMenuDslBuilder(type: MenuType) : MenuBuilderDsl(type) {
    private val pageButtons = mutableObjectListOf<Button>()
    var buttonRange: SlotRange = SlotRange(Slot(0), Slot(type.size - 1))
    var previousPageButtonSlot = Slot(0)
    var nextPageButtonSlot = Slot(1)
    private var noPageItemStack: ItemStack = ItemStack.EMPTY

    /**
     * Add a button to the paginated menu.
     *
     * @param builder The builder lambda for creating the button.
     */
    fun pageButton(builder: @ButtonBuilderDslMarker ButtonDslBuilder.() -> Unit) {
        pageButtons.add(Button(builder))
    }

    fun pageButtons(buttons: List<Button>) {
        pageButtons.addAll(buttons)
    }

    fun pageButtons(vararg buttons: Button) {
        pageButtons.addAll(buttons)
    }

    fun pageButtons(amount: Int, builder: @ButtonBuilderDslMarker ButtonDslBuilder.(Int) -> Unit) {
        repeat(amount) { pageButton { builder(it) } }
    }

    fun noPageItemStack(itemStack: ItemStack) {
        noPageItemStack = itemStack
    }

    fun noPageItemStack(builder: @ItemBuilderDslMarker ItemBuilder.() -> Unit) {
        noPageItemStack(ItemBuilder().apply(builder).build())
    }



    @PublishedApi
    override fun build() = PaginatedMenu(
        name,
        type,
        pageButtons,
        buttonRange,
        previousPageButtonSlot,
        nextPageButtonSlot,
        noPageItemStack
    )
}

fun main() {

    val protectionMembers = listOf("Alice", "Bob", "Charlie")
    val protectionButtons = protectionMembers.map {
        Button {
            item(ItemBuilder(ItemTypes.PLAYER_HEAD) { })
        }
    }

    val menu = paginatedMenu(MenuType.GENERIC9X5) {
        buttons(slot(0)..slot(8)) {
            item { type(ItemTypes.BLACK_STAINED_GLASS_PANE) }
        }

        buttons(slot(32)..slot(40)) {
            item { type(ItemTypes.BLACK_STAINED_GLASS_PANE) }
        }

        pageButtons(protectionButtons) // Add a list of buttons to the menu

        pageButton {  // add a single button to the page buttons
            item {
                type(ItemTypes.DIRT)
            }
        }

        pageButtons(5) {  // add 5 buttons to the page buttons
            itemBuilder {
                itemType = ItemTypes.DIRT
                amount = max(1, it)
            }
        }

        button(4) {
            item(
                ItemStack.builder().type(ItemTypes.CHEST)
                    .component(ComponentTypes.ITEM_NAME, Component.text("Test")).build()
            )
        }

        noPageItemStack { // set the item stack for the no page item
            itemType = ItemTypes.BARRIER
        }
    }
}