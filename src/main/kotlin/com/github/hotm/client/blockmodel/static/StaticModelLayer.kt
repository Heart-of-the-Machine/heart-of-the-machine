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

        fun createBlock(
            rotationContainer: ModelBakeSettings,
            renderMaterial: RenderMaterial,
            rotate: Boolean,
            cullFaces: Boolean,
            sideDepth: Float,
            faceDepth: Float,
            down: Sprite,
            up: Sprite,
            north: Sprite,
            south: Sprite,
            west: Sprite,
            east: Sprite
        ): StaticModelLayer {
            val renderer = RendererAccess.INSTANCE.renderer
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

                emitter.spriteBake(
                    0, when (normal) {
                        Direction.DOWN -> down
                        Direction.UP -> up
                        Direction.NORTH -> north
                        Direction.SOUTH -> south
                        Direction.WEST -> west
                        Direction.EAST -> east
                    }, MutableQuadView.BAKE_LOCK_UV or EXTRA_FLAGS_PER_AXIS[normal.axis.ordinal]
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