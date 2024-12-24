package net.craftoriya.packetuxui.menu.utils

import net.craftoriya.packetuxui.menu.menu.MenuType

class PositionRange(
    override val start: Slot,
    override val endInclusive: Slot
) : ClosedRange<Slot>, Iterable<Slot> {

    override fun iterator(): Iterator<Slot> {
        return object : Iterator<Slot> {
            private var current = start

            override fun hasNext(): Boolean = current <= endInclusive

            override fun next(): Slot {
                if (!hasNext()) throw NoSuchElementException()

                val result = current
                current = current.next()

                return result
            }
        }
    }
}

class Slot() : Cloneable, Comparable<Slot> {

    var x: Int = 0
        set(value) {
            require(value >= 0) { "X must be greater or equal to 0" }
            require(value < gridWidth) { "X must be smaller than grid width" }
            field = value
        }

    var y: Int = 0
        set(value) {
            require(value >= 0) { "Y must be greater or equal to 0" }
            field = value
        }

    var gridWidth: Int = 9
        set(value) {
            require(value >= 0) { "Grid width must be greater or equal to 0" }
            require(x < value) { "X must be smaller than grid width" }
            field = value
        }

    constructor(x: Int, y: Int, gridWidth: Int = 9) : this() {
        this.x = x
        this.y = y
        this.gridWidth = gridWidth
    }

    constructor(absolute: Int) : this() {
        fromSlot(absolute)
    }

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

    fun toSlot() = y * gridWidth + x

    fun normalize(block: () -> Unit): Slot {
        block()

        if (x >= gridWidth || x < 0) {
            y += x / gridWidth
            x %= gridWidth
        }

        return this
    }

    fun down(amount: Int) = this.normalize {
        y += amount
    }

    fun up(amount: Int) = this.normalize {
        y -= amount
    }

    fun left(amount: Int) = this.normalize {
        x -= amount
    }

    fun right(amount: Int) = this.normalize {
        x += amount
    }

    fun down() = down(1)
    fun up() = up(1)
    fun left() = left(1)
    fun right() = right(1)

    fun next() = Slot().apply {
        this@apply.fromSlot(this@Slot.toSlot() + 1)
    }

    fun previous() = Slot().apply {
        this@apply.fromSlot(this@Slot.toSlot() - 1)
    }

    fun isValid() = x >= 0 && y >= 0

    fun fits(menuType: MenuType) = fits(menuType.size)
    fun fits(size: Int): Boolean = toSlot() in 0..<size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Slot

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    public override fun clone() = Slot(x, y, gridWidth)

    override fun toString(): String {
        return "Position(x=$x, y=$y, absolute=${toSlot()})"
    }

    operator fun plus(other: Slot) = Slot(x + other.x, y + other.y)
    operator fun minus(other: Slot) = Slot(x - other.x, y - other.y)

    override fun compareTo(other: Slot) = toSlot().compareTo(other.toSlot())

    operator fun rangeTo(other: Slot): PositionRange {
        require(this <= other) { "Range start must be less than or equal to range end." }

        return PositionRange(this, other)
    }

    operator fun rangeTo(other: Int): PositionRange {
        require(this.toSlot() <= other) { "Range start must be less than or equal to range end." }

        return PositionRange(this, Slot(other))
    }
}

fun slot(x: Int, y: Int, gridWidth: Int = 9) = Slot(x, y, gridWidth)
infix fun Int.at(y: Int) = Slot(this, y)
infix fun Slot.width(width: Int) {
    this.gridWidth = width
}

