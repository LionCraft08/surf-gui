package dev.slne.surf.gui.util

import dev.slne.surf.gui.common.objectSetOf
import dev.slne.surf.gui.menu.menu.MenuBuilderDsl
import dev.slne.surf.gui.menu.menu.MenuType
import it.unimi.dsi.fastutil.objects.ObjectSet

/**
 * Represents a range of [Slot]s, providing functionality to iterate over all slots in the range.
 *
 * This class implements [ClosedRange] for range-like operations and [Iterable] to allow iteration over the slots.
 *
 * @property start The first [Slot] in the range.
 * @property endInclusive The last [Slot] in the range.
 */
class SlotRange(
    override val start: Slot,
    override val endInclusive: Slot
) : ClosedRange<Slot>, Iterable<Slot> {

    init {
        require(start <= endInclusive) { "Start slot must be less than or equal to the end slot." }
    }

    /**
     * Returns an iterator over the slots in the range.
     *
     * @return An [Iterator] that iterates from the `start` to the `endInclusive` [Slot].
     */
    override fun iterator() = object : Iterator<Slot> {
        private var current = start

        override fun hasNext(): Boolean = current <= endInclusive

        override fun next(): Slot {
            if (!hasNext()) throw NoSuchElementException()

            val result = current
            current = current.next()

            return result
        }
    }

    /**
     * Converts this range into a set of all [Slot]s contained in it.
     *
     * @return A [it.unimi.dsi.fastutil.objects.ObjectSet] of [Slot]s from `start` to `endInclusive`.
     */
    fun toList(): ObjectSet<Slot> = objectSetOf(iterator())

    /**
     * Returns the number of slots in the range.
     *
     * @return The total number of slots from `start` to `endInclusive`.
     */
    fun size(): Int = endInclusive.toSlot() - start.toSlot() + 1

    fun compatibleWith(menuType: MenuType): Boolean = start.fits(menuType) && endInclusive.fits(menuType) && size() <= menuType.size
}

/**
 * Represents a position in a grid with immutable coordinates.
 *
 * @property x The horizontal position (column) in the grid. Must be between 0 and `gridWidth - 1`.
 * @property y The vertical position (row) in the grid. Must be greater than or equal to 0.
 * @property gridWidth The width of the grid. Must be greater than 0.
 * @constructor Creates a new [Slot] with the given `x`, `y`, and `gridWidth`.
 * @throws kotlin.IllegalArgumentException if `x` or `y` are out of range or `gridWidth` is invalid.
 */
data class Slot(
    val x: Int,
    val y: Int,
    val gridWidth: Int = 9
) : Comparable<Slot> {

    init {
        require(x in 0 until gridWidth) { "X must be between 0 and gridWidth - 1" }
        require(y >= 0) { "Y must be greater or equal to 0" }
        require(gridWidth > 0) { "Grid width must be greater than 0" }
    }

    /**
     * Creates a new [Slot] from an absolute position in the grid.
     *
     * @param absolute The absolute position in the grid.
     * @param gridWidth The width of the grid. Defaults to 9.
     * @throws kotlin.IllegalArgumentException if `gridWidth` is invalid.
     */
    constructor(absolute: Int, gridWidth: Int = 9) : this(
        x = absolute % gridWidth,
        y = absolute / gridWidth,
        gridWidth = gridWidth
    )

    /**
     * Converts the current [Slot] into its absolute position in the grid.
     *
     * @return The absolute position as an [Int].
     */
    fun toSlot(): Int = y * gridWidth + x

    /**
     * Normalizes the [Slot] so that `x` and `y` remain within valid bounds of the grid.
     *
     * @return A new [Slot] instance with normalized values.
     */
    private fun normalize(): Slot {
        val normalizedX = x.mod(gridWidth)
        val normalizedY = y + x / gridWidth
        return copy(x = normalizedX, y = normalizedY)
    }

    /**
     * Returns a new [Slot] moved down by the specified amount.
     *
     * @param amount The number of rows to move down. Defaults to 1.
     * @return A new [Slot] instance with the updated position.
     */
    fun down(amount: Int = 1): Slot = copy(y = y + amount).normalize()

    /**
     * Returns a new [Slot] moved up by the specified amount.
     *
     * @param amount The number of rows to move up. Defaults to 1.
     * @return A new [Slot] instance with the updated position.
     */
    fun up(amount: Int = 1): Slot = copy(y = y - amount).normalize()

    /**
     * Returns a new [Slot] moved left by the specified amount.
     *
     * @param amount The number of columns to move left. Defaults to 1.
     * @return A new [Slot] instance with the updated position.
     */
    fun left(amount: Int = 1): Slot = copy(x = x - amount).normalize()

    /**
     * Returns a new [Slot] moved right by the specified amount.
     *
     * @param amount The number of columns to move right. Defaults to 1.
     * @return A new [Slot] instance with the updated position.
     */
    fun right(amount: Int = 1): Slot = copy(x = x + amount).normalize()

    /**
     * Returns the next [Slot] in the grid, moving one step forward.
     *
     * @return A new [Slot] instance representing the next position.
     */
    fun next(): Slot = Slot(toSlot() + 1, gridWidth)

    /**
     * Returns the previous [Slot] in the grid, moving one step backward.
     *
     * @return A new [Slot] instance representing the previous position.
     */
    fun previous(): Slot = Slot(toSlot() - 1, gridWidth)

    /**
     * Checks if the [Slot] position is valid (i.e., `x` and `y` are non-negative).
     *
     * @return `true` if the [Slot] is valid; otherwise, `false`.
     */
    fun isValid(): Boolean = x >= 0 && y >= 0

    /**
     * Checks if the [Slot] fits within a grid of the specified [MenuType].
     *
     * @param menuType The [MenuType] to check against.
     * @return `true` if the [Slot] fits; otherwise, `false`.
     */
    fun fits(menuType: MenuType) = fits(menuType.size)

    /**
     * Checks if the [Slot] fits within a grid of the specified size.
     *
     * @param size The total number of slots in the grid.
     * @return `true` if the [Slot] fits; otherwise, `false`.
     */
    fun fits(size: Int): Boolean = toSlot() in 0 until size

    /**
     * Compares this [Slot] with another [Slot] based on their absolute positions.
     *
     * @param other The other [Slot] to compare with.
     * @return A negative value if this [Slot] is less, zero if equal, and a positive value if greater.
     */
    override fun compareTo(other: Slot): Int = toSlot().compareTo(other.toSlot())

    /**
     * Adds another [Slot] to this one, returning a new [Slot] with the summed position.
     *
     * @param other The [Slot] to add.
     * @return A new [Slot] instance with the summed position.
     */
    operator fun plus(other: Slot): Slot = copy(x = x + other.x, y = y + other.y).normalize()

    /**
     * Subtracts another [Slot] from this one, returning a new [Slot] with the difference.
     *
     * @param other The [Slot] to subtract.
     * @return A new [Slot] instance with the resulting position.
     */
    operator fun minus(other: Slot): Slot = copy(x = x - other.x, y = y - other.y).normalize()

    /**
     * Creates a range of [Slot]s from this one to the specified [other].
     *
     * @param other The end [Slot] of the range.
     * @return A [SlotRange] representing the range from this [Slot] to [other].
     * @throws kotlin.IllegalArgumentException if this [Slot] is greater than [other].
     */
    operator fun rangeTo(other: Slot): SlotRange {
        require(this <= other) { "Range start must be less than or equal to range end." }
        return SlotRange(this, other)
    }

    /**
     * Creates a range of [Slot]s from this one to the specified [other].
     *
     * @param other The end position of the range.
     * @return A [SlotRange] representing the range from this [Slot] to [other].
     * @throws kotlin.IllegalArgumentException if this [Slot] is greater than [other].
     */
    operator fun rangeTo(other: Int): SlotRange {
        require(this.toSlot() <= other) { "Range start must be less than or equal to range end." }
        return SlotRange(this, Slot(other))
    }
}

fun slot(x: Int, y: Int, gridWidth: Int = 9) = Slot(x, y, gridWidth)
fun slot(absolute: Int, gridWidth: Int = 9) = Slot(absolute, gridWidth)
infix fun Int.at(y: Int) = Slot(this, y)
infix fun Slot.width(width: Int) = copy(gridWidth = width)

fun MenuBuilderDsl.slot(x: Int, y: Int) = Slot(x, y, this.type.size)
fun MenuBuilderDsl.slot(absolute: Int) = Slot(absolute, this.type.size)
