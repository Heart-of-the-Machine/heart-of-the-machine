package com.github.hotm.mod.datagen

import java.util.concurrent.CompletableFuture
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_LEYLINE
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider
import net.minecraft.registry.HolderLookup
import net.minecraft.registry.tag.BlockTags

class BlockTagGen(
    output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>
) : BlockTagProvider(output, registriesFuture) {
    override fun configure(arg: HolderLookup.Provider) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(
            THINKING_STONE,
            THINKING_SCRAP,
            RUSTED_THINKING_SCRAP,
            PLASSEIN_THINKING_SCRAP,
            THINKING_STONE_LEYLINE,
            THINKING_SCRAP_LEYLINE,
            RUSTED_THINKING_SCRAP_LEYLINE,
            PLASSEIN_THINKING_SCRAP_LEYLINE
        )

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(
            THINKING_STONE,
            THINKING_SCRAP,
            RUSTED_THINKING_SCRAP,
            PLASSEIN_THINKING_SCRAP,
            THINKING_STONE_LEYLINE,
            THINKING_SCRAP_LEYLINE,
            RUSTED_THINKING_SCRAP_LEYLINE,
            PLASSEIN_THINKING_SCRAP_LEYLINE
        )
    }
}
