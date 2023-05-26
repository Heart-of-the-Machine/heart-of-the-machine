package com.github.hotm.mod.datagen

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.Log
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_LEYLINE
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import net.minecraft.block.Block
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.model.*
import net.minecraft.util.Identifier
import com.kneelawk.kmodlib.client.blockmodel.JsonMaterial
import com.kneelawk.kmodlib.client.blockmodel.JsonTexture
import com.kneelawk.kmodlib.client.blockmodel.KUnbakedModel
import com.kneelawk.kmodlib.client.blockmodel.UnbakedLayeredModel
import com.kneelawk.kmodlib.client.blockmodel.connector.RenderTagModelConnector
import com.kneelawk.kmodlib.client.blockmodel.ct.UnbakedCTLayer
import com.kneelawk.kmodlib.client.blockmodel.cube.UnbakedCubeAllModelLayer
import com.kneelawk.kmodlib.client.blockmodel.sprite.UnbakedStaticSpriteSupplier

class BlockModelGen(output: FabricDataOutput) : FabricModelProvider(output) {
    private val LEYLINE_ECTEX = id("block/leyline_exterior_corners")
    private val LEYLINE_HETEX = id("block/leyline_horizontal_edges")
    private val LEYLINE_ICTEX = id("block/leyline_interior_corners")
    private val LEYLINE_VETEX = id("block/leyline_vertical_edges")

    override fun generateBlockStateModels(gen: BlockStateModelGenerator) {
        gen.registerSimpleCubeAll(THINKING_STONE)
        gen.registerSimpleCubeAll(THINKING_SCRAP)

        Models.CUBE_BOTTOM_TOP.upload(
            RUSTED_THINKING_SCRAP,
            Texture.sideTopBottom(RUSTED_THINKING_SCRAP)
                .put(TextureKey.BOTTOM, ModelIds.getBlockModelId(THINKING_SCRAP)),
            gen.modelCollector
        )

        gen.registerSimpleState(RUSTED_THINKING_SCRAP)
        gen.registerSimpleState(PLASSEIN_THINKING_SCRAP)

        gen.registerLeyline(THINKING_STONE_LEYLINE, THINKING_STONE)
        gen.registerLeyline(THINKING_SCRAP_LEYLINE, THINKING_SCRAP)

        gen.registerSimpleState(RUSTED_THINKING_SCRAP_LEYLINE)
        gen.excludeFromSimpleItemModelGeneration(RUSTED_THINKING_SCRAP_LEYLINE)
        gen.registerSimpleState(PLASSEIN_THINKING_SCRAP_LEYLINE)
        gen.excludeFromSimpleItemModelGeneration(PLASSEIN_THINKING_SCRAP_LEYLINE)
    }

    private fun BlockStateModelGenerator.registerRandomHorizontalRotationsState(block: Block) {
        blockStateCollector.accept(
            VariantsBlockStateSupplier.create(
                block, *BlockStateModelGenerator.createModelVariantWithRandomHorizontalRotations(
                    ModelIds.getBlockModelId(block)
                )
            )
        )
    }

    private fun BlockStateModelGenerator.registerLeyline(block: Block, base: Block) {
        registerSimpleState(block)

        val suffixedModelId = ModelIds.getBlockModelId(block).extendPath(".kr")
        val baseId = ModelIds.getBlockModelId(base)

        val baseLayer = UnbakedCubeAllModelLayer(baseId, JsonMaterial.DEFAULT, 0f, true, true, true)

        val leylineBlockLayer = UnbakedCTLayer(
            LEYLINE_ECTEX,
            LEYLINE_ICTEX,
            LEYLINE_HETEX,
            LEYLINE_VETEX,
            null,
            JsonMaterial(BlendMode.CUTOUT, false, true, false, true),
            0f,
            true,
            false,
            0,
            RenderTagModelConnector(id("leyline"))
        )

        val blockModel = UnbakedLayeredModel(Identifier("block/block"), baseId, listOf(baseLayer, leylineBlockLayer))
        registerCustomModel(suffixedModelId, blockModel, KUnbakedModel.CODEC)

        val leylineItemLayer = UnbakedCubeAllModelLayer(
            JsonTexture(UnbakedStaticSpriteSupplier(LEYLINE_ECTEX), 0),
            JsonMaterial(BlendMode.CUTOUT, false, true, false, true),
            0f,
            true,
            true,
            true
        )

        val itemModel = UnbakedLayeredModel(Identifier("block/block"), baseId, listOf(baseLayer, leylineItemLayer))
        registerCustomModel(ModelIds.getItemModelId(block.asItem()).extendPath(".kr"), itemModel, KUnbakedModel.CODEC)

        excludeFromSimpleItemModelGeneration(block)
    }

    private fun <T> BlockStateModelGenerator.registerCustomModel(id: Identifier, model: T, codec: Codec<T>) {
        val element = codec.encodeStart(JsonOps.INSTANCE, model).getOrThrow(false, Log.LOG::error)
        modelCollector.accept(id) { element }
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
    }
}
