package com.github.hotm.client.blockmodel.ct

import com.github.hotm.client.blockmodel.BakedModelLayer
import com.github.hotm.util.DirectionUtils.texDown
import com.github.hotm.util.DirectionUtils.texLeft
import com.github.hotm.util.DirectionUtils.texRight
import com.github.hotm.util.DirectionUtils.texUp
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.block.BlockState
import net.minecraft.client.texture.Sprite
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockRenderView
import java.util.*
import java.util.function.Supplier

class CTModelLayer(
    private val sprites: Array<Sprite>,
    private val material: RenderMaterial
) : BakedModelLayer {
    private data class QuadPos(val left: Float, val bottom: Float, val right: Float, val top: Float) {
        fun emit(emitter: QuadEmitter, face: Direction, depth: Float) {
            emitter.square(face, left, bottom, right, top, depth)
        }
    }

    companion object {
        private val CORNERS = arrayOf(
            QuadPos(0.0f, 0.0f, 0.5f, 0.5f),
            QuadPos(0.5f, 0.0f, 1.0f, 0.5f),
            QuadPos(0.0f, 0.5f, 0.5f, 1.0f),
            QuadPos(0.5f, 0.5f, 1.0f, 1.0f)
        )

        private val CORNERS_PER_AXIS = arrayOf(
            CORNERS,
            CORNERS,
            arrayOf(CORNERS[1], CORNERS[0], CORNERS[3], CORNERS[2]),
        )

        private val EXTRA_FLAGS_PER_AXIS = arrayOf(
            0,
            MutableQuadView.BAKE_FLIP_V,
            0,
        )
    }

    private val doCorners = sprites.size > 4

    override fun emitBlockQuads(
        blockView: BlockRenderView,
        state: BlockState,
        pos: BlockPos,
        randomSupplier: Supplier<Random>,
        context: RenderContext
    ) {
        val emitter = context.emitter

        for (normal in Direction.values()) {
            val axis = normal.axis.ordinal
            val indices = getIndices(blockView, pos, normal)

            for (corner in 0 until 4) {
                CORNERS_PER_AXIS[axis][corner].emit(emitter, normal, 0.0f)
                emitter.spriteBake(
                    0,
                    sprites[(indices shr (corner * 3)) and 0x7],
                    MutableQuadView.BAKE_LOCK_UV or EXTRA_FLAGS_PER_AXIS[axis]
                )
                emitter.spriteColor(0, -1, -1, -1, -1)
                emitter.material(material)

                emitter.emit()
            }
        }
    }

    override fun emitItemQuads(stack: ItemStack, randomSupplier: Supplier<Random>, context: RenderContext) {
        // This layer doesn't render items
    }

    private fun getIndices(blockView: BlockRenderView, pos: BlockPos, normal: Direction): Int {
        val horizontals = getHorizontals(blockView, pos, normal)
        val verticals = getVerticals(blockView, pos, normal)
        val corners = if (doCorners) {
            getCorners(blockView, pos, normal) and verticals and horizontals
        } else {
            0
        }

        return (corners shl 2) or (horizontals xor corners) or ((verticals xor corners) shl 1)
    }

    private fun getHorizontals(blockView: BlockRenderView, pos: BlockPos, normal: Direction): Int {
        val block = blockView.getBlockState(pos).block
        val right = blockView.getBlockState(pos.offset(normal.texRight())).isOf(block)
                && !blockView.getBlockState(pos.offset(normal.texRight()).offset(normal)).isOf(block)
        val left = blockView.getBlockState(pos.offset(normal.texLeft())).isOf(block)
                && !blockView.getBlockState(pos.offset(normal.texLeft()).offset(normal)).isOf(block)

        return if (left) {
            0x41
        } else {
            0
        } or if (right) {
            0x208
        } else {
            0
        }
    }

    private fun getVerticals(blockView: BlockRenderView, pos: BlockPos, normal: Direction): Int {
        val block = blockView.getBlockState(pos).block
        val up = blockView.getBlockState(pos.offset(normal.texUp())).isOf(block)
                && !blockView.getBlockState(pos.offset(normal.texUp()).offset(normal)).isOf(block)
        val down = blockView.getBlockState(pos.offset(normal.texDown())).isOf(block)
                && !blockView.getBlockState(pos.offset(normal.texDown()).offset(normal)).isOf(block)

        return if (down) {
            0x9
        } else {
            0
        } or if (up) {
            0x240
        } else {
            0
        }
    }

    private fun getCorners(blockView: BlockRenderView, pos: BlockPos, normal: Direction): Int {
        val block = blockView.getBlockState(pos).block
        val bl = blockView.getBlockState(pos.offset(normal.texDown()).offset(normal.texLeft())).isOf(block)
                && !blockView.getBlockState(pos.offset(normal.texDown()).offset(normal.texLeft()).offset(normal))
            .isOf(block)
        val br = blockView.getBlockState(pos.offset(normal.texDown()).offset(normal.texRight())).isOf(block)
                && !blockView.getBlockState(pos.offset(normal.texDown()).offset(normal.texRight()).offset(normal))
            .isOf(block)
        val tl = blockView.getBlockState(pos.offset(normal.texUp()).offset(normal.texLeft())).isOf(block)
                && !blockView.getBlockState(pos.offset(normal.texUp()).offset(normal.texLeft()).offset(normal))
            .isOf(block)
        val tr = blockView.getBlockState(pos.offset(normal.texUp()).offset(normal.texRight())).isOf(block)
                && !blockView.getBlockState(pos.offset(normal.texUp()).offset(normal.texRight()).offset(normal))
            .isOf(block)

        return if (bl) {
            0x1
        } else {
            0
        } or if (br) {
            0x8
        } else {
            0
        } or if (tl) {
            0x40
        } else {
            0
        } or if (tr) {
            0x200
        } else {
            0
        }
    }
}