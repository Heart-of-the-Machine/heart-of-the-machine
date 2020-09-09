package com.github.hotm.util

import net.minecraft.util.math.Direction

object DirectionUtils {
    private val UPS = arrayOf(
        Direction.SOUTH,
        Direction.NORTH,
        Direction.UP,
        Direction.UP,
        Direction.UP,
        Direction.UP
    )

    private val DOWNS: Array<Direction> = UPS.map { it.opposite }.toTypedArray()

    private val RIGHTS = arrayOf(
        Direction.EAST,
        Direction.EAST,
        Direction.WEST,
        Direction.EAST,
        Direction.SOUTH,
        Direction.NORTH
    )

    private val LEFTS: Array<Direction> = RIGHTS.map { it.opposite }.toTypedArray()

    fun Direction.texUp(): Direction {
        return UPS[ordinal]
    }

    fun Direction.texDown(): Direction {
        return DOWNS[ordinal]
    }

    fun Direction.texRight(): Direction {
        return RIGHTS[ordinal]
    }

    fun Direction.texLeft(): Direction {
        return LEFTS[ordinal]
    }
}