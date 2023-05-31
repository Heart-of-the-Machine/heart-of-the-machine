package com.github.hotm.mod.datagen

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_SAND
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_LEYLINE
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider
import net.minecraft.registry.HolderLookup
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import java.util.concurrent.CompletableFuture

class BlockTagGen(
    output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>
) : BlockTagProvider(output, registriesFuture) {
    companion object {
        private val NECTERE_CARVER_REPLACABLES = TagKey.of(RegistryKeys.BLOCK, id("nectere_carver_replaceables"))

        private val BASE_BLOCKS = arrayOf(
            THINKING_STONE,
            THINKING_SCRAP,
            RUSTED_THINKING_SCRAP,
            PLASSEIN_THINKING_SCRAP,
            THINKING_STONE_LEYLINE,
            THINKING_SCRAP_LEYLINE,
            RUSTED_THINKING_SCRAP_LEYLINE,
            PLASSEIN_THINKING_SCRAP_LEYLINE
        )

        private val BASE_TAGS = arrayOf(
            BlockTags.PICKAXE_MINEABLE,
            BlockTags.NEEDS_STONE_TOOL,
            NECTERE_CARVER_REPLACABLES
        )
    }

    override fun configure(arg: HolderLookup.Provider) {
        for (tag in BASE_TAGS) {
            getOrCreateTagBuilder(tag).add(*BASE_BLOCKS)
        }

        getOrCreateTagBuilder(NECTERE_CARVER_REPLACABLES).add(THINKING_SAND)
    }
}
