package net.craftoriya.packetuxui.menu.menu.page

import com.github.retrooper.packetevents.protocol.component.ComponentTypes
import com.github.retrooper.packetevents.protocol.item.ItemStack
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import it.unimi.dsi.fastutil.objects.ObjectList
import net.craftoriya.packetuxui.common.int2ObjectMapOf
import net.craftoriya.packetuxui.common.mutableObjectListOf
import net.craftoriya.packetuxui.common.toObjectList
import net.craftoriya.packetuxui.menu.button.Button
import net.craftoriya.packetuxui.menu.button.ButtonBuilderDslMarker
import net.craftoriya.packetuxui.menu.button.ButtonDslBuilder
import net.craftoriya.packetuxui.menu.item.ItemBuilder
import net.craftoriya.packetuxui.menu.item.ItemBuilderDslMarker
import net.craftoriya.packetuxui.menu.menu.Menu
import net.craftoriya.packetuxui.menu.menu.MenuBuilderDsl
import net.craftoriya.packetuxui.menu.menu.MenuBuilderDslMarker
import net.craftoriya.packetuxui.menu.menu.MenuType
import net.craftoriya.packetuxui.menu.utils.Slot
import net.craftoriya.packetuxui.menu.utils.SlotRange
import net.craftoriya.packetuxui.menu.utils.slot
import net.kyori.adventure.text.Component
import kotlin.math.max

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
    private val nextPageItemStack: ItemStack = createNextPageStack(),
    private val previousPageItemStack: ItemStack = createPreviousPageStack()
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
    private var nextPageItemStack: ItemStack = createNextPageStack()
    private var previousPageItemStack: ItemStack = createPreviousPageStack()

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

    fun nextPageItemStack(itemStack: ItemStack) {
        nextPageItemStack = itemStack
    }

    fun nextPageItemStack(builder: @ItemBuilderDslMarker ItemBuilder.() -> Unit) {
        nextPageItemStack(ItemBuilder().apply(builder).build())
    }

    fun previousPageItemStack(itemStack: ItemStack) {
        previousPageItemStack = itemStack
    }

    fun previousPageItemStack(builder: @ItemBuilderDslMarker ItemBuilder.() -> Unit) {
        previousPageItemStack(ItemBuilder().apply(builder).build())
    }

    @PublishedApi
    override fun build() = PaginatedMenu(
        name,
        type,
        pageButtons,
        buttonRange,
        previousPageButtonSlot,
        nextPageButtonSlot,
        noPageItemStack,
        nextPageItemStack,
        previousPageItemStack
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

        nextPageItemStack {
            itemType = ItemTypes.ARROW
            name = Component.text("Next Page (custom set)")
        }

        previousPageItemStack {
            itemType = ItemTypes.ARROW
            name = Component.text("Previous Page (custom set)")
        }
    }
}