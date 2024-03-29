package com.github.hotm.mod.datagen

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
import com.github.hotm.mod.block.HotMBlocks.SOLAR_ARRAY_LEAVES
import com.github.hotm.mod.block.HotMBlocks.SOLAR_ARRAY_SPROUT
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
import com.github.hotm.mod.item.HotMItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.enchantment.Enchantments
import net.minecraft.loot.condition.TableBonusLootCondition
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.ApplyBonusLootFunction
import net.minecraft.loot.function.LimitCountLootFunction
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.operator.BoundedIntUnaryOperator
import net.minecraft.loot.provider.number.UniformLootNumberProvider

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
        addDrop(SMOOTH_THINKING_STONE_LEYLINE)

        addDrop(THINKING_SAND)
        addDrop(THINKING_GLASS)

        addDrop(SMOOTH_THINKING_STONE)
        addDrop(THINKING_STONE_BRICKS)
        addDrop(THINKING_STONE_TILES)
        addDrop(SMOOTH_THINKING_STONE_STAIRS)
        addDrop(THINKING_STONE_BRICK_STAIRS)
        addDrop(THINKING_STONE_TILE_STAIRS)
        addDrop(OBELISK_PART)
        addDrop(GLOWY_OBELISK_PART)

        add(AURA_CRYSTAL) {
            dropsWithSilkTouch(
                it,
                applyExplosionDecay(
                    it,
                    ItemEntry.builder(HotMItems.AURA_CRYSTAL_SHARD)
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0f, 4.0f)))
                        .apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
                        .apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 4)))
                )
            )
        }
        add(HOLO_CRYSTAL) {
            dropsWithSilkTouch(
                it,
                applyExplosionDecay(
                    it,
                    ItemEntry.builder(HotMItems.HOLO_CRYSTAL_SHARD)
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0f, 4.0f)))
                        .apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
                        .apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 4)))
                )
            )
        }
        addDrop(AURA_LAMP)
        addDrop(HOLO_LAMP)
        addDrop(AURA_THINKING_STONE)
        addDrop(HOLO_THINKING_STONE)

        addDrop(SOLAR_ARRAY_STEM)
        addDrop(SOLAR_ARRAY_SPROUT)

        add(SMOOTH_THINKING_STONE_SLAB) { slabDrops(it) }
        add(THINKING_STONE_BRICK_SLAB) { slabDrops(it) }
        add(THINKING_STONE_TILE_SLAB) { slabDrops(it) }
        add(SOLAR_ARRAY_LEAVES) {
            dropsWithShearsOrSilkTouch(
                it,
                applySurvivesExplosionCondition(it, ItemEntry.builder(SOLAR_ARRAY_SPROUT)).conditionally(
                    TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.05F, 0.0625F, 0.083333336F, 0.1F)
                )
            )
        }
    }
}
