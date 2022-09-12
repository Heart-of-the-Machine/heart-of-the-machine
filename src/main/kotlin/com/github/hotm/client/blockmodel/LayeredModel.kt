package com.github.hotm.client.blockmodel

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.block.BlockState
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.render.model.json.ModelOverrideList
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.texture.Sprite
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.random.RandomGenerator
import net.minecraft.world.BlockRenderView
import java.util.function.Supplier

class LayeredModel(
    private val transformation: ModelTransformation,
    private val particle: Sprite,
    private val layers: Array<BakedModelLayer>
) : BakedModel, FabricBakedModel {
    override fun getQuads(state: BlockState?, face: Direction?, random: RandomGenerator): List<BakedQuad> {
        return listOf()
    }

    override fun useAmbientOcclusion(): Boolean {
        return false
    }

    override fun hasDepth(): Boolean {
        return false
    }

    override fun isSideLit(): Boolean {
        // For correct item rendering
        return true
    }

    override fun isBuiltin(): Boolean {
        return false
    }

    override fun getParticleSprite(): Sprite {
        return particle
    }

    override fun getTransformation(): ModelTransformation {
        return transformation
    }

    override fun getOverrides(): ModelOverrideList {
        return ModelOverrideList.EMPTY
    }

    override fun isVanillaAdapter(): Boolean {
        return false
    }

    override fun emitBlockQuads(
        blockView: BlockRenderView,
        state: BlockState,
        pos: BlockPos,
        randomSupplier: Supplier<RandomGenerator>,
        context: RenderContext
    ) {
        for (layer in layers) {
            layer.emitBlockQuads(blockView, state, pos, randomSupplier, context)
        }
    }

    override fun emitItemQuads(stack: ItemStack, randomSupplier: Supplier<RandomGenerator>, context: RenderContext) {
        for (layer in layers) {
            layer.emitItemQuads(stack, randomSupplier, context)
        }
    }
}
