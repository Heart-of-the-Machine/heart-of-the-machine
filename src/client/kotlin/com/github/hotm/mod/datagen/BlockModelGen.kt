package com.github.hotm.mod.datagen

import com.github.hotm.mod.block.HotMBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.model.BlockStateModelGenerator
import net.minecraft.data.client.model.ModelIds
import net.minecraft.data.client.model.TextureKey
import net.minecraft.data.client.model.TexturedModel
import net.minecraft.data.client.model.VariantsBlockStateSupplier

class BlockModelGen(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(gen: BlockStateModelGenerator) {
        gen.registerSimpleCubeAll(HotMBlocks.THINKING_STONE)
        gen.registerSimpleCubeAll(HotMBlocks.THINKING_SCRAP)

        gen.registerRandomHorizontalRotations(TexturedModel.CUBE_BOTTOM_TOP.updateTexture {
            it.put(
                TextureKey.BOTTOM,
                ModelIds.getBlockModelId(HotMBlocks.THINKING_SCRAP)
            )
        }, HotMBlocks.RUSTED_THINKING_SCRAP)

        gen.blockStateCollector.accept(
            VariantsBlockStateSupplier.create(
                HotMBlocks.PLASSEIN_THINKING_SCRAP,
                *BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(
                    ModelIds.getBlockModelId(HotMBlocks.PLASSEIN_THINKING_SCRAP)
                )
            )
        )
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
    }
}
