package com.github.hotm.mod.datagen

import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.PLASSEIN_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.RUSTED_THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP_LEYLINE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_LEYLINE
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider

class BlockLootGen(dataOutput: FabricDataOutput) : FabricBlockLootTableProvider(dataOutput) {
    override fun generate() {
        addDrop(THINKING_STONE)
        addDrop(THINKING_SCRAP)
        addDrop(RUSTED_THINKING_SCRAP)
        addDrop(PLASSEIN_THINKING_SCRAP)
        addDrop(THINKING_STONE_LEYLINE)
        addDrop(THINKING_SCRAP_LEYLINE)
        addDrop(RUSTED_THINKING_SCRAP_LEYLINE)
        addDrop(PLASSEIN_THINKING_SCRAP_LEYLINE)
    }
}
