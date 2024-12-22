package net.craftoriya.packetuxui.state

interface State<V> {
    operator fun setValue(thisRef: Any?, property: Any?, value: V?)
    operator fun getValue(thisRef: Any?, property: Any?): V?

    fun getDefaultValue(): V?

    fun notNull(): NonNullState<V> {
        return object : NonNullState<V> {
            override fun setValue(thisRef: Any?, property: Any?, value: V) {
                this@State.setValue(thisRef, property, value)
            }

            override fun getValue(thisRef: Any?, property: Any?): V {
                return this@State.getValue(thisRef, property) ?: getDefaultValue()
            }

            override fun getDefaultValue(): V {
                return this@State.getDefaultValue()
                    ?: error("Default value is not set for this state")
            }
        }
    }
}

interface NonNullState<V> {
    operator fun getValue(thisRef: Any?, property: Any?): V
    operator fun setValue(thisRef: Any?, property: Any?, value: V)

    fun getDefaultValue(): V
}

class AbstractState<V>(private val defaultValue: V?) : State<V> {

    private var value: V? = null

    override fun setValue(thisRef: Any?, property: Any?, value: V?) {
        this.value = value
    }

    override fun getValue(thisRef: Any?, property: Any?): V? {
        return value ?: defaultValue
    }

    override fun getDefaultValue(): V? {
        return defaultValue
    }
}

fun <T> state(defaultValue: T?) = AbstractState(defaultValue)
fun intState(defaultValue: Int?) = AbstractState(defaultValue)
fun stringState(defaultValue: String?) = AbstractState(defaultValue)
fun booleanState(defaultValue: Boolean?) = AbstractState(defaultValue)
fun floatState(defaultValue: Float?) = AbstractState(defaultValue)
fun doubleState(defaultValue: Double?) = AbstractState(defaultValue)
fun longState(defaultValue: Long?) = AbstractState(defaultValue)
fun shortState(defaultValue: Short?) = AbstractState(defaultValue)
fun byteState(defaultValue: Byte?) = AbstractState(defaultValue)
fun charState(defaultValue: Char?) = AbstractState(defaultValue)


