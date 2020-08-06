package com.github.hotm.util

import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i

/**
 * Represents one of the four cardinal directions.
 */
enum class CardinalDirection(
    val id: Int,
    val idOpposite: Int,
    val directionName: String,
    val axisDirection: Direction.AxisDirection,
    val axis: Direction.Axis,
    val vector: Vec3i
) {
    NORTH(0, 1, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, Vec3i(0, 0, -1)),
    SOUTH(1, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, Vec3i(0, 0, 1)),
    WEST(2, 3, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, Vec3i(-1, 0, 0)),
    EAST(3, 2, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, Vec3i(1, 0, 0));

    val direction: Direction by lazy { Direction.byId(id + 2) }
}