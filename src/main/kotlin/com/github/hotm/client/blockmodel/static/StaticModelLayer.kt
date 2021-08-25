package com.github.hotm.client.blockmodel.static

import com.github.hotm.client.blockmodel.BakedModelLayer
import com.github.hotm.client.blockmodel.util.QuadPos
import net.fabricmc.fabric.api.renderer.v1.RendererAccess
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.block.BlockState
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.texture.Sprite
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.world.BlockRenderView
import java.util.*
import java.util.function.Supplier

class StaticModelLayer(private val mesh: Mesh) : BakedModelLayer {
    companion object {
        init {
            println("### PLEASE REMOVE THE COMMENTED OUT PARTS OF StaticModelLayer ###")
        }
//        private val SPRITE_UV = arrayOf(
//            Vec2f(0f, 0f),
//            Vec2f(0f, 1f),
//            Vec2f(1f, 1f),
//            Vec2f(1f, 0f)
//        )

        fun createBlock(
            rotationContainer: ModelBakeSettings,
            renderMaterial: RenderMaterial,
            rotate: Boolean,
            cullFaces: Boolean,
            sideDepth: Float,
            faceDepth: Float,
            quarterFaces: Boolean,
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
                val sprite = when (normal) {
                    Direction.DOWN -> down
                    Direction.UP -> up
                    Direction.NORTH -> north
                    Direction.SOUTH -> south
                    Direction.WEST -> west
                    Direction.EAST -> east
                }
                val tintIndex = when (normal) {
                    Direction.DOWN -> downTintIndex
                    Direction.UP -> upTintIndex
                    Direction.NORTH -> northTintIndex
                    Direction.SOUTH -> southTintIndex
                    Direction.WEST -> westTintIndex
                    Direction.EAST -> eastTintIndex
                }

                if (quarterFaces) {
                    buildQuarteredFace(
                        emitter,
                        normal,
                        rotationContainer,
                        renderMaterial,
                        rotate,
                        cullFaces,
                        sideDepth,
                        faceDepth,
                        sprite,
                        tintIndex
                    )
                } else {
                    buildFace(
                        emitter,
                        normal,
                        rotationContainer,
                        renderMaterial,
                        rotate,
                        cullFaces,
                        sideDepth,
                        faceDepth,
                        sprite,
                        tintIndex,
                    )
                }
            }

            return StaticModelLayer(meshBuilder.build())
        }

        private fun getCorners(sideDepth: Float, faceDepth: Float): Array<QuadPos> {
            val depthClamped = MathHelper.clamp(sideDepth, 0.0f, 0.5f)
            val depthMaxed = faceDepth.coerceAtMost(0.5f)
            return arrayOf(
                QuadPos(0.0f + depthClamped, 0.0f + depthClamped, 0.5f, 0.5f, depthMaxed),
                QuadPos(0.5f, 0.0f + depthClamped, 1.0f - depthClamped, 0.5f, depthMaxed),
                QuadPos(0.0f + depthClamped, 0.5f, 0.5f, 1.0f - depthClamped, depthMaxed),
                QuadPos(0.5f, 0.5f, 1.0f - depthClamped, 1.0f - depthClamped, depthMaxed)
            )
        }

        private fun buildQuarteredFace(
            emitter: QuadEmitter,
            normal: Direction,
            rotationContainer: ModelBakeSettings,
            renderMaterial: RenderMaterial,
            rotate: Boolean,
            cullFaces: Boolean,
            sideDepth: Float,
            faceDepth: Float,
            sprite: Sprite,
            tintIndex: Int
        ) {
            val corners = getCorners(sideDepth, faceDepth)
            for (corner in corners) {
                corner.emit(emitter, normal, if (rotate) rotationContainer else null)
                putQuadSettings(emitter, renderMaterial, tintIndex, sprite, cullFaces, rotationContainer, normal)
            }
        }

        private fun buildFace(
            emitter: QuadEmitter,
            normal: Direction,
            rotationContainer: ModelBakeSettings,
            renderMaterial: RenderMaterial,
            rotate: Boolean,
            cullFaces: Boolean,
            sideDepth: Float,
            faceDepth: Float,
            sprite: Sprite,
            tintIndex: Int,
        ) {
            QuadPos(0.0f + sideDepth, 0.0f + sideDepth, 1.0f - sideDepth, 1.0f - sideDepth, faceDepth)
                .emit(emitter, normal, if (rotate) rotationContainer else null)
            putQuadSettings(emitter, renderMaterial, tintIndex, sprite, cullFaces, rotationContainer, normal)
        }

        private fun putQuadSettings(
            emitter: QuadEmitter,
            renderMaterial: RenderMaterial,
            tintIndex: Int,
            sprite: Sprite,
            cullFaces: Boolean,
            rotationContainer: ModelBakeSettings,
            normal: Direction
        ) {
            emitter.spriteColor(0, -1, -1, -1, -1)
            emitter.material(renderMaterial)
//            emitter.sprite(0, 0, SPRITE_UV[0].x, SPRITE_UV[0].y)
//            emitter.sprite(1, 0, SPRITE_UV[1].x, SPRITE_UV[1].y)
//            emitter.sprite(2, 0, SPRITE_UV[2].x, SPRITE_UV[2].y)
//            emitter.sprite(3, 0, SPRITE_UV[3].x, SPRITE_UV[3].y)

            emitter.colorIndex(tintIndex)

            emitter.spriteBake(0, sprite, MutableQuadView.BAKE_NORMALIZED)

            emitter.cullFace(
                if (cullFaces) {
                    Direction.transform(rotationContainer.rotation.matrix, normal)
                } else {
                    null
                }
            )

            emitter.emit()
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