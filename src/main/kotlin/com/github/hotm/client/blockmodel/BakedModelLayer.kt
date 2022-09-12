package com.github.hotm.client.blockmodel

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.random.RandomGenerator
import net.minecraft.world.BlockRenderView
import java.util.function.Supplier

interface BakedModelLayer {
    fun emitBlockQuads(
        blockView: BlockRenderView,
        state: BlockState,
        pos: BlockPos,
        randomSupplier: Supplier<RandomGenerator>,
        context: RenderContext
    )

    fun emitItemQuads(stack: ItemStack, randomSupplier: Supplier<RandomGenerator>, context: RenderContext)
}
