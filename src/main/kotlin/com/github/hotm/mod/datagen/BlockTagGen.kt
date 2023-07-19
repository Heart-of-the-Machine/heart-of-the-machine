package com.github.hotm.mod.datagen

import com.github.hotm.mod.block.HotMBlockTags
import com.github.hotm.mod.block.HotMBlockTags.NECTERE_CARVER_REPLACABLES
import com.github.hotm.mod.block.HotMBlocks.AURA_CRYSTAL
import com.github.hotm.mod.block.HotMBlocks.AURA_LAMP
import com.github.hotm.mod.block.HotMBlocks.AURA_THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.GLOWY_OBELISK_PART
import com.github.hotm.mod.block.HotMBlocks.HOLO_CRYSTAL
import com.github.hotm.mod.block.HotMBlocks.HOLO_LAMP
import com.github.hotm.mod.block.HotMBlocks.HOLO_THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.OBELISK_PART
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.SMOOTH_THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.SMOOTH_THINKING_STONE_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.SMOOTH_THINKING_STONE_SLAB
import com.github.hotm.mod.block.HotMBlocks.SMOOTH_THINKING_STONE_STAIRS
import com.github.hotm.mod.block.HotMBlocks.SOLAR_ARRAY_STEM
import com.github.hotm.mod.block.HotMBlocks.THINKING_GLASS
import com.github.hotm.mod.block.HotMBlocks.THINKING_SAND
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_BRICKS
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_BRICK_SLAB
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_BRICK_STAIRS
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_TILES
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_TILE_SLAB
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_TILE_STAIRS
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider
import net.minecraft.registry.HolderLookup
import net.minecraft.registry.tag.BlockTags
import java.util.concurrent.CompletableFuture

class BlockTagGen(
    output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>
) : BlockTagProvider(output, registriesFuture) {
    companion object {
        private val BASE_BLOCKS = arrayOf(
            THINKING_STONE,
            THINKING_SCRAP,
            RUSTED_THINKING_SCRAP,
            PLASSEIN_THINKING_SCRAP,
            THINKING_STONE_LEYLINE,
            THINKING_SCRAP_LEYLINE,
            RUSTED_THINKING_SCRAP_LEYLINE,
            PLASSEIN_THINKING_SCRAP_LEYLINE,
            SMOOTH_THINKING_STONE_LEYLINE
        )

        private val AESTHETIC_BLOCKS = arrayOf(
            SMOOTH_THINKING_STONE,
            THINKING_STONE_BRICKS,
            THINKING_STONE_TILES,
            SMOOTH_THINKING_STONE_STAIRS,
            THINKING_STONE_BRICK_STAIRS,
            THINKING_STONE_TILE_STAIRS,
            SMOOTH_THINKING_STONE_SLAB,
            THINKING_STONE_BRICK_SLAB,
            THINKING_STONE_TILE_SLAB,
            OBELISK_PART,
            GLOWY_OBELISK_PART,
            THINKING_GLASS,
            AURA_CRYSTAL,
            HOLO_CRYSTAL,
            AURA_LAMP,
            HOLO_LAMP,
            AURA_THINKING_STONE,
            HOLO_THINKING_STONE
        )

        private val BASE_TAGS = arrayOf(
            BlockTags.PICKAXE_MINEABLE,
            BlockTags.NEEDS_STONE_TOOL,
            NECTERE_CARVER_REPLACABLES
        )

        private val AESTHETIC_TAGS = arrayOf(BlockTags.PICKAXE_MINEABLE, BlockTags.NEEDS_STONE_TOOL)
    }

    override fun configure(arg: HolderLookup.Provider) {
        for (tag in BASE_TAGS) {
            getOrCreateTagBuilder(tag).add(*BASE_BLOCKS)
        }

        getOrCreateTagBuilder(NECTERE_CARVER_REPLACABLES).add(THINKING_SAND)
        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE).add(THINKING_SAND)

        for (tag in AESTHETIC_TAGS) {
            getOrCreateTagBuilder(tag).add(*AESTHETIC_BLOCKS)
        }

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(SOLAR_ARRAY_STEM)
        getOrCreateTagBuilder(HotMBlockTags.PLASSEIN_SOURCE).add(SOLAR_ARRAY_STEM)

        getOrCreateTagBuilder(HotMBlockTags.LEYLINES).add(
            THINKING_STONE_LEYLINE,
            THINKING_SCRAP_LEYLINE,
            RUSTED_THINKING_SCRAP_LEYLINE,
            PLASSEIN_THINKING_SCRAP_LEYLINE,
            SMOOTH_THINKING_STONE_LEYLINE
        )

        getOrCreateTagBuilder(HotMBlockTags.PLASSEIN_FERTILE).add(
            PLASSEIN_THINKING_SCRAP,
            PLASSEIN_THINKING_SCRAP_LEYLINE,
            RUSTED_THINKING_SCRAP,
            RUSTED_THINKING_SCRAP_LEYLINE,
            THINKING_SCRAP,
            THINKING_SCRAP_LEYLINE
        )
    }
}
