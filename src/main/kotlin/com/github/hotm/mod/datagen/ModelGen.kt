package com.github.hotm.mod.datagen

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.HotMLog
import com.github.hotm.mod.block.HotMBlockFamilies
import com.github.hotm.mod.block.HotMBlocks.AURA_CRYSTAL
import com.github.hotm.mod.block.HotMBlocks.AURA_LAMP
import com.github.hotm.mod.block.HotMBlocks.AURA_THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.GLOWY_OBELISK_PART
import com.github.hotm.mod.block.HotMBlocks.HOLO_CRYSTAL
import com.github.hotm.mod.block.HotMBlocks.HOLO_LAMP
import com.github.hotm.mod.block.HotMBlocks.HOLO_THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.NECTERE_PORTAL_SPAWNER
import com.github.hotm.mod.block.HotMBlocks.OBELISK_PART
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.SMOOTH_THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.SMOOTH_THINKING_STONE_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.SOLAR_ARRAY_LEAVES
import com.github.hotm.mod.block.HotMBlocks.SOLAR_ARRAY_SPROUT
import com.github.hotm.mod.block.HotMBlocks.SOLAR_ARRAY_STEM
import com.github.hotm.mod.block.HotMBlocks.SPOROFRUIT
import com.github.hotm.mod.block.HotMBlocks.THINKING_GLASS
import com.github.hotm.mod.block.HotMBlocks.THINKING_SAND
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_BRICKS
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_TILES
import com.github.hotm.mod.item.HotMItems.AURAMETER
import com.github.hotm.mod.item.HotMItems.AURA_CRYSTAL_SHARD
import com.github.hotm.mod.item.HotMItems.HOLO_CRYSTAL_SHARD
import com.github.hotm.mod.item.HotMItems.NODE_TUNER
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import net.minecraft.block.Block
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.model.BlockStateModelGenerator
import net.minecraft.data.client.model.ModelIds
import net.minecraft.data.client.model.Models
import net.minecraft.data.client.model.Texture
import net.minecraft.data.client.model.TextureKey
import net.minecraft.data.client.model.TexturedModel
import net.minecraft.data.client.model.VariantsBlockStateSupplier
import net.minecraft.util.Identifier
import com.kneelawk.kmodlib.client.blockmodel.JsonMaterial
import com.kneelawk.kmodlib.client.blockmodel.JsonTexture
import com.kneelawk.kmodlib.client.blockmodel.KUnbakedModel
import com.kneelawk.kmodlib.client.blockmodel.UnbakedLayeredModel
import com.kneelawk.kmodlib.client.blockmodel.connector.RenderTagModelConnector
import com.kneelawk.kmodlib.client.blockmodel.ct.UnbakedCTLayer
import com.kneelawk.kmodlib.client.blockmodel.cube.UnbakedCubeAllModelLayer
import com.kneelawk.kmodlib.client.blockmodel.modelref.UnbakedModelRefModelLayer
import com.kneelawk.kmodlib.client.blockmodel.sprite.UnbakedStaticSpriteSupplier

class ModelGen(output: FabricDataOutput) : FabricModelProvider(output) {
    companion object {
        private val LEYLINE_ECTEX = id("block/leyline_exterior_corners")
        private val LEYLINE_HETEX = id("block/leyline_horizontal_edges")
        private val LEYLINE_ICTEX = id("block/leyline_interior_corners")
        private val LEYLINE_VETEX = id("block/leyline_vertical_edges")
    }

    override fun generateBlockStateModels(gen: BlockStateModelGenerator) {
        gen.registerSimpleCubeAll(THINKING_STONE)
        gen.registerSimpleCubeAll(THINKING_SCRAP)
        gen.registerSimpleCubeAll(NECTERE_PORTAL_SPAWNER)
        gen.registerSimpleCubeAll(THINKING_SAND)

        gen.registerSimpleCubeAll(AURA_CRYSTAL)
        gen.registerSimpleCubeAll(HOLO_CRYSTAL)
        gen.registerSimpleCubeAll(AURA_LAMP)
        gen.registerSimpleCubeAll(HOLO_LAMP)
        gen.registerParentedItemModel(
            AURA_THINKING_STONE,
            ModelIds.getBlockModelId(AURA_THINKING_STONE).extendPath("_y")
        )
        gen.registerParentedItemModel(
            HOLO_THINKING_STONE,
            ModelIds.getBlockModelId(HOLO_THINKING_STONE).extendPath("_y")
        )

        gen.registerCubeAllModelTexturePool(SMOOTH_THINKING_STONE).family(HotMBlockFamilies.SMOOTH_THINKING_STONE)
        gen.registerCubeAllModelTexturePool(THINKING_STONE_BRICKS).family(HotMBlockFamilies.THINKING_STONE_BRICKS)
        gen.registerCubeAllModelTexturePool(THINKING_STONE_TILES).family(HotMBlockFamilies.THINKING_STONE_TILES)

        gen.registerAxisRotated(
            OBELISK_PART,
            TexturedModel.END_FOR_TOP_CUBE_COLUMN,
            TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL
        )
        gen.registerAxisRotated(
            GLOWY_OBELISK_PART,
            TexturedModel.END_FOR_TOP_CUBE_COLUMN,
            TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL
        )

        gen.registerLog(SOLAR_ARRAY_STEM).stem(SOLAR_ARRAY_STEM)
        gen.registerSingleton(SOLAR_ARRAY_LEAVES, TexturedModel.LEAVES)

        Models.CUBE_BOTTOM_TOP.upload(
            RUSTED_THINKING_SCRAP,
            Texture.sideTopBottom(RUSTED_THINKING_SCRAP)
                .put(TextureKey.BOTTOM, ModelIds.getBlockModelId(THINKING_SCRAP)),
            gen.modelCollector
        )

        gen.registerSimpleState(RUSTED_THINKING_SCRAP)
        gen.registerSimpleState(PLASSEIN_THINKING_SCRAP)
        gen.registerSimpleState(THINKING_GLASS)

        gen.registerLeyline(THINKING_STONE_LEYLINE, THINKING_STONE)
        gen.registerLeyline(THINKING_SCRAP_LEYLINE, THINKING_SCRAP)
        gen.registerLeyline(SMOOTH_THINKING_STONE_LEYLINE, SMOOTH_THINKING_STONE)

        gen.registerSimpleState(RUSTED_THINKING_SCRAP_LEYLINE)
        gen.excludeFromSimpleItemModelGeneration(RUSTED_THINKING_SCRAP_LEYLINE)
        gen.registerSimpleState(PLASSEIN_THINKING_SCRAP_LEYLINE)
        gen.excludeFromSimpleItemModelGeneration(PLASSEIN_THINKING_SCRAP_LEYLINE)

        Models.CUBE_ALL.upload(
            ModelIds.getItemModelId(THINKING_GLASS.asItem()),
            Texture.all(id("block/thinking_glass_item")),
            gen.modelCollector
        )

        gen.registerTintableCross(SOLAR_ARRAY_SPROUT, BlockStateModelGenerator.TintType.NOT_TINTED)

        gen.registerHighlightedCross(SPOROFRUIT)
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

        val suffixedModelId = ModelIds.getBlockModelId(block).extendPath(".kml")
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
        registerCustomModel(ModelIds.getItemModelId(block.asItem()).extendPath(".kml"), itemModel, KUnbakedModel.CODEC)

        excludeFromSimpleItemModelGeneration(block)
    }

    private fun BlockStateModelGenerator.registerHighlightedCross(
        block: Block, baseSuffix: String = "_base", highlightSuffix: String = "_highlight",
        baseTint: BlockStateModelGenerator.TintType = BlockStateModelGenerator.TintType.NOT_TINTED,
        highlightTint: BlockStateModelGenerator.TintType = BlockStateModelGenerator.TintType.NOT_TINTED
    ) {
        val id = ModelIds.getBlockModelId(block)
        val baseId = id.extendPath(baseSuffix)
        val highlightId = id.extendPath(highlightSuffix)
        baseTint.crossModel.upload(baseId, Texture.cross(baseId), modelCollector)
        highlightTint.crossModel.upload(highlightId, Texture.cross(highlightId), modelCollector)
        registerSimpleState(block)
        registerHighlighted(id, baseId, highlightId)

        val itemId = ModelIds.getItemModelId(block.asItem())
        val baseItemId = itemId.extendPath(baseSuffix)
        val highlightItemId = itemId.extendPath(highlightSuffix)
        Models.SINGLE_LAYER_ITEM.upload(baseItemId, Texture.layer0(baseId), modelCollector)
        Models.SINGLE_LAYER_ITEM.upload(highlightItemId, Texture.layer0(highlightId), modelCollector)
        excludeFromSimpleItemModelGeneration(block)
        registerHighlighted(
            itemId,
            baseItemId,
            highlightItemId,
            particle = baseId,
            transformation = Identifier("item/generated"),
            sideLit = false
        )
    }

    private fun BlockStateModelGenerator.registerHighlighted(
        modelId: Identifier, base: Identifier, highlight: Identifier, particle: Identifier = highlight,
        transformation: Identifier = Identifier("block/block"), sideLit: Boolean = true
    ) {
        val suffixedModelId = modelId.extendPath(".kml")

        val highlightMaterial = JsonMaterial(BlendMode.DEFAULT, false, true, false, true)

        val baseLayer = UnbakedModelRefModelLayer(base, JsonMaterial.DEFAULT, true)
        val highlightLayer = UnbakedModelRefModelLayer(highlight, highlightMaterial, true)

        val blockModel = UnbakedLayeredModel(transformation, particle, listOf(baseLayer, highlightLayer), sideLit)
        registerCustomModel(suffixedModelId, blockModel, KUnbakedModel.CODEC)
    }

    private fun <T> BlockStateModelGenerator.registerCustomModel(id: Identifier, model: T, codec: Codec<T>) {
        val element = codec.encodeStart(JsonOps.INSTANCE, model).getOrThrow(false, HotMLog.LOG::error)
        modelCollector.accept(id) { element }
    }

    override fun generateItemModels(gen: ItemModelGenerator) {
        gen.register(AURA_CRYSTAL_SHARD, Models.SINGLE_LAYER_ITEM)
        gen.register(HOLO_CRYSTAL_SHARD, Models.SINGLE_LAYER_ITEM)

        gen.register(AURAMETER, Models.SINGLE_LAYER_ITEM)
        gen.register(NODE_TUNER, Models.SINGLE_LAYER_ITEM)
    }
}
