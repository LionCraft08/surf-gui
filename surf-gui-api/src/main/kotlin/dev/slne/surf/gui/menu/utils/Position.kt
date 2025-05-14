package dev.slne.surf.gui.menu.utils

import dev.slne.surf.gui.menu.menu.MenuType
import dev.slne.surf.gui.util.SlotRange

/**
 * Represents a range of positions.
 *
 * @property start The start of the range.
 * @property endInclusive The end of the range.
 *
 * @see Position
 * @see ClosedRange
 * @see Iterable
 */
@Deprecated(replaceWith = ReplaceWith("SlotRange"), message = "Not useful")
class PositionRange(
    override val start: Position,
    override val endInclusive: Position
) : ClosedRange<Position>, Iterable<Position> {

    override fun iterator(): Iterator<Position> {
        return object : Iterator<Position> {
            private var current = start

            override fun hasNext(): Boolean = current <= endInclusive

            override fun next(): Position {
                if (!hasNext()) throw NoSuchElementException()

                val result = current
                current = current.next()

                return result
            }
        }
    }
}

/**
 * Represents a position in a grid.
 */
@Deprecated(replaceWith = ReplaceWith("Slot"), message = "Not useful")
class Position() : Cloneable, Comparable<Position> {

    /**
     * The x coordinate of the position.
     */
    var x: Int = 0
        /**
         * Sets the x coordinate of the position.
         *
         * @param value The new x coordinate.
         * @throws IllegalArgumentException If the x coordinate is less than 0 or greater than or equal to the grid width
         */
        set(value) {
            require(value >= 0) { "X must be greater or equal to 0" }
            require(value < gridWidth) { "X must be smaller than grid width" }
            field = value
        }

    /**
     * The y coordinate of the position.
     */
    var y: Int = 0
        /**
         * Sets the y coordinate of the position.
         *
         * @param value The new y coordinate.
         * @throws IllegalArgumentException If the y coordinate is less than 0
         */
        set(value) {
            require(value >= 0) { "Y must be greater or equal to 0" }
            field = value
        }

    /**
     * The width of the grid.
     */
    var gridWidth: Int = 9
        /**
         * Sets the width of the grid.
         *
         * @param value The new width of the grid.
         * @throws IllegalArgumentException If the width of the grid is less than 0 or the x coordinate is greater than or equal to the grid width
         */
        set(value) {
            require(value >= 0) { "Grid width must be greater or equal to 0" }
            require(x < value) { "X must be smaller than grid width" }
            field = value
        }

    /**
     * Creates a new position with the specified x and y coordinates and grid width.
     *
     * @param x The x coordinate of the position.
     * @param y The y coordinate of the position.
     * @param gridWidth The width of the grid.
     */
    constructor(x: Int, y: Int, gridWidth: Int = 9) : this() {
        this.x = x
        this.y = y
        this.gridWidth = gridWidth
    }

    /**
     * Creates a new position from the specified absolute slot.
     *
     * @param absolute The absolute slot.
     */
    constructor(absolute: Int) : this() {
        fromSlot(absolute)
    }

    /**
     * Sets the x and y coordinates of the position from the specified slot.
     *
     * @param slot The slot.
     * @throws IllegalArgumentException If the slot is less than 0
     * @throws RuntimeException If the slot input and output do not match
     */
    fun fromSlot(slot: Int) {
        require(slot >= 0) { "Slot must be greater or equal to 0" }

        var x = slot
        var y = 0

        while (x >= gridWidth) {
            x -= gridWidth
            y++
        }

        this.y = y
        this.x = x

        if (toSlot() != slot) {
            throw RuntimeException("Slot input and output do not match")
        }
    }

    /**
     * Returns the absolute slot of the position.
     *
     * @return The absolute slot of the position.
     */
    fun toSlot() = y * gridWidth + x

    /**
     * Normalizes the position by executing the specified block.
     *
     * @param block The block to execute.
     * @return The normalized position.
     */
    fun normalize(block: () -> Unit): Position {
        block()

        if (x >= gridWidth || x < 0) {
            y += x / gridWidth
            x %= gridWidth
        }

        return this
    }

    /**
     * Moves the position down by the specified amount.
     *
     * @param amount The amount to move down.
     * @return The normalized position.
     */
    fun down(amount: Int) = this.normalize {
        y += amount
    }

    /**
     * Moves the position up by the specified amount.
     *
     * @param amount The amount to move up.
     * @return The normalized position.
     */
    fun up(amount: Int) = this.normalize {
        y -= amount
    }

    /**
     * Moves the position left by the specified amount.
     *
     * @param amount The amount to move left.
     * @return The normalized position.
     */
    fun left(amount: Int) = this.normalize {
        x -= amount
    }

    /**
     * Moves the position right by the specified amount.
     *
     * @param amount The amount to move right.
     * @return The normalized position.
     */
    fun right(amount: Int) = this.normalize {
        x += amount
    }

    /**
     * Moves the position down by one.
     *
     * @return The normalized position.
     */
    fun down() = down(1)

    /**
     * Moves the position up by one.
     *
     * @return The normalized position.
     */
    fun up() = up(1)

    /**
     * Moves the position left by one.
     *
     * @return The normalized position.
     */
    fun left() = left(1)

    /**
     * Moves the position right by one.
     *
     * @return The normalized position.
     */
    fun right() = right(1)

    /**
     * Returns the next position.
     *
     * @return The next position.
     */
    fun next() = Position().apply {
        this@apply.fromSlot(this@Position.toSlot() + 1)
    }

    /**
     * Returns the previous position.
     *
     * @return The previous position.
     */
    fun previous() = Position().apply {
        this@apply.fromSlot(this@Position.toSlot() - 1)
    }

    /**
     * Checks if the position is valid.
     *
     * @return `true` if the position is valid, `false` otherwise.
     */
    fun isValid() = x >= 0 && y >= 0

    /**
     * Checks if the position fits the specified menu type.
     *
     * @param menuType The menu type to check.
     * @return `true` if the position fits the menu type, `false` otherwise.
     */
    fun fits(menuType: MenuType) = fits(menuType.size)

    /**
     * Checks if the position fits the specified size.
     *
     * @param size The size to check.
     * @return `true` if the position fits the size, `false` otherwise.
     */
    fun fits(size: Int): Boolean = toSlot() in 0..<size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    public override fun clone() = Position(x, y, gridWidth)

    override fun toString(): String {
        return "Position(x=$x, y=$y, absolute=${toSlot()})"
    }

    operator fun plus(other: Position) = Position(x + other.x, y + other.y)
    operator fun minus(other: Position) = Position(x - other.x, y - other.y)

    override fun compareTo(other: Position) = toSlot().compareTo(other.toSlot())

    operator fun rangeTo(other: Position): PositionRange {
        require(this <= other) { "Range start must be less than or equal to range end." }

        return PositionRange(this, other)
    }

    operator fun rangeTo(other: Int): PositionRange {
        require(this.toSlot() <= other) { "Range start must be less than or equal to range end." }

        return PositionRange(this, Position(other))
    }
}

/**
 * Creates a new position with the specified x and y coordinates and grid width.
 *
 * @param x The x coordinate of the position.
 * @param y The y coordinate of the position.
 * @param gridWidth The width of the grid.
 * @return The created position.
 */
@Deprecated(replaceWith = ReplaceWith("SlotRange"), message = "Not useful")
fun position(x: Int, y: Int, gridWidth: Int = 9) = Position(x, y, gridWidth)


