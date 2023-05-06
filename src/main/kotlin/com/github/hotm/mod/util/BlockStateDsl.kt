package com.github.hotm.mod.util

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.state.property.Property

class BlockStateBuilder(private var state: BlockState) {
    fun build() = state

    infix fun <T : Comparable<T>, V : T> Property<T>.with(value: V) {
        state = state.with(this, value)
    }
}

fun blockState(state: BlockState, configure: BlockStateBuilder.() -> Unit): BlockState =
    BlockStateBuilder(state).apply(configure).build()

fun blockState(block: Block, configure: BlockStateBuilder.() -> Unit): BlockState =
    blockState(block.defaultState, configure)
