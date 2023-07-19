package com.github.hotm.mod.datagen

import com.github.hotm.mod.block.HotMBlocks
import com.github.hotm.mod.item.HotMItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.recipe.RecipeCategory
import java.util.function.Consumer

class RecipeGen(output: FabricDataOutput) : FabricRecipeProvider(output) {
    override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
        offerTwoByTwoCompactingRecipe(
            exporter,
            RecipeCategory.BUILDING_BLOCKS,
            HotMBlocks.AURA_CRYSTAL,
            HotMItems.AURA_CRYSTAL_SHARD
        )
        offerTwoByTwoCompactingRecipe(
            exporter,
            RecipeCategory.BUILDING_BLOCKS,
            HotMBlocks.HOLO_CRYSTAL,
            HotMItems.HOLO_CRYSTAL_SHARD
        )
    }
}
