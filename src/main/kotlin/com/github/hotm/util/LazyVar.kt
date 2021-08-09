package com.github.hotm.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LazyVar<T>(initializer: () -> T) : ReadWriteProperty<Any?, T> {
    private var initializer: (() -> T)? = initializer
    private var onSet: ((T) -> Unit)? = null
    private var value: Any? = Uninitialized

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return if (value == Uninitialized) {
            synchronized(this) {
                if (value == Uninitialized) {
                    return initializer!!().also {
                        value = it
                        initializer = null
                    }
                } else {
                    value as T
                }
            }
        } else {
            value as T
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        onSet?.invoke(value)
        synchronized(this) {
            this.value = value
        }
    }

    infix fun onSet(onSet: (T) -> Unit): LazyVar<T> {
        this.onSet = onSet
        return this
    }

    private object Uninitialized
}

fun <T> lazyVar(initializer: () -> T): LazyVar<T> = LazyVar(initializer)
