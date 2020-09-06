package com.github.hotm.client.blockmodel.static

import com.github.hotm.client.blockmodel.BakedModelLayer
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockRenderView
import java.util.*
import java.util.function.Supplier

class StaticModelLayer(private val mesh: Mesh) : BakedModelLayer {
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