package com.github.hotm.client.blockmodel.static

import com.github.hotm.client.blockmodel.BakedModelLayer
import com.github.hotm.client.blockmodel.QuadEmitterUtils
import net.fabricmc.fabric.api.renderer.v1.RendererAccess
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.block.BlockState
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.texture.Sprite
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec2f
import net.minecraft.world.BlockRenderView
import java.util.*
import java.util.function.Supplier

class StaticModelLayer(private val mesh: Mesh) : BakedModelLayer {
    companion object {
        private val EXTRA_FLAGS_PER_AXIS = arrayOf(
            0,
            MutableQuadView.BAKE_FLIP_V,
            0,
        )

        private val SPRITE_UV = arrayOf(
            Vec2f(0f, 0f),
            Vec2f(0f, 1f),
            Vec2f(1f, 1f),
            Vec2f(1f, 0f)
        )

        fun createBlock(
            rotationContainer: ModelBakeSettings,
            renderMaterial: RenderMaterial,
            rotate: Boolean,
            cullFaces: Boolean,
            sideDepth: Float,
            faceDepth: Float,
            down: Sprite,
            downTintIndex: Int,
            up: Sprite,
            upTintIndex: Int,
            north: Sprite,
            northTintIndex: Int,
            south: Sprite,
            southTintIndex: Int,
            west: Sprite,
            westTintIndex: Int,
            east: Sprite,
            eastTintIndex: Int
        ): StaticModelLayer {
            val renderer = RendererAccess.INSTANCE.renderer!!
            val meshBuilder = renderer.meshBuilder()
            val emitter = meshBuilder.emitter

            for (normal in Direction.values()) {
                if (rotate) {
                    QuadEmitterUtils.square(
                        emitter,
                        rotationContainer,
                        normal,
                        0.0f + sideDepth,
                        0.0f + sideDepth,
                        1.0f - sideDepth,
                        1.0f - sideDepth,
                        faceDepth
                    )
                } else {
                    emitter.square(
                        normal,
                        0.0f + sideDepth,
                        0.0f + sideDepth,
                        1.0f - sideDepth,
                        1.0f - sideDepth,
                        faceDepth
                    )
                }

                emitter.spriteColor(0, -1, -1, -1, -1)
                emitter.material(renderMaterial)
                emitter.sprite(0, 0, SPRITE_UV[0].x, SPRITE_UV[0].y)
                emitter.sprite(1, 0, SPRITE_UV[1].x, SPRITE_UV[1].y)
                emitter.sprite(2, 0, SPRITE_UV[2].x, SPRITE_UV[2].y)
                emitter.sprite(3, 0, SPRITE_UV[3].x, SPRITE_UV[3].y)

                emitter.colorIndex(
                    when (normal) {
                        Direction.DOWN -> downTintIndex
                        Direction.UP -> upTintIndex
                        Direction.NORTH -> northTintIndex
                        Direction.SOUTH -> southTintIndex
                        Direction.WEST -> westTintIndex
                        Direction.EAST -> eastTintIndex
                    }
                )

                emitter.spriteBake(
                    0, when (normal) {
                        Direction.DOWN -> down
                        Direction.UP -> up
                        Direction.NORTH -> north
                        Direction.SOUTH -> south
                        Direction.WEST -> west
                        Direction.EAST -> east
                    }, MutableQuadView.BAKE_NORMALIZED
                )

                emitter.cullFace(
                    if (cullFaces) {
                        Direction.transform(rotationContainer.rotation.matrix, normal)
                    } else {
                        null
                    }
                )

                emitter.emit()
            }

            return StaticModelLayer(meshBuilder.build())
        }
    }

    override fun emitBlockQuads(
        blockView: BlockRenderView,
        state: BlockState,
        pos: BlockPos,
        randomSupplier: Supplier<Random>,
        context: RenderContext
    ) {
        context.meshConsumer().accept(mesh)
    }

    override fun emitItemQuads(stack: ItemStack, randomSupplier: Supplier<Random>, context: RenderContext) {
        context.meshConsumer().accept(mesh)
    }
}