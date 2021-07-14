package com.github.hotm.world

import net.minecraft.util.math.BlockPos

object HotMPortalOffsets {
    private const val PORTAL_OFFSET_X = 2
    private const val PORTAL_OFFSET_Y = 1
    private const val PORTAL_OFFSET_Z = 2

    fun structure2PortalX(x: Int): Int = x + PORTAL_OFFSET_X
    fun structure2PortalY(y: Int): Int = y + PORTAL_OFFSET_Y
    fun structure2PortalZ(z: Int): Int = z + PORTAL_OFFSET_Z

    fun portal2StructureX(x: Int): Int = x - PORTAL_OFFSET_X
    fun portal2StructureY(y: Int): Int = y - PORTAL_OFFSET_Y
    fun portal2StructureZ(z: Int): Int = z - PORTAL_OFFSET_Z

    fun structure2PortalPos(pos: BlockPos): BlockPos = pos.add(PORTAL_OFFSET_X, PORTAL_OFFSET_Y, PORTAL_OFFSET_Z)
    fun portal2StructurePos(pos: BlockPos): BlockPos = pos.add(-PORTAL_OFFSET_X, -PORTAL_OFFSET_Y, -PORTAL_OFFSET_Z)

    fun transform2PortalPos(
        transformX: (Int, Int) -> Int,
        transformY: (Int) -> Int,
        transformZ: (Int, Int) -> Int
    ): BlockPos {
        return BlockPos(
            transformX(PORTAL_OFFSET_X, PORTAL_OFFSET_Z),
            transformY(PORTAL_OFFSET_Y),
            transformZ(PORTAL_OFFSET_X, PORTAL_OFFSET_Z)
        )
    }
}