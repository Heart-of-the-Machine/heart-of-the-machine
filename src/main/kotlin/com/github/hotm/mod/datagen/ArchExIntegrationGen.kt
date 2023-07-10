package com.github.hotm.mod.datagen

import java.util.concurrent.CompletableFuture
import com.github.hotm.mod.block.HotMBlocks.SMOOTH_THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.THINKING_SCRAP
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_BRICKS
import com.github.hotm.mod.block.HotMBlocks.THINKING_STONE_TILES
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.registry.Registries

class ArchExIntegrationGen(output: FabricDataOutput) : DataProvider {
    companion object {
        private val INTEGRATION_BLOCKS = listOf(
            THINKING_STONE,
            THINKING_SCRAP,
            SMOOTH_THINKING_STONE,
            THINKING_STONE_BRICKS,
            THINKING_STONE_TILES
        )

        private val TYPES = listOf(
            "arch",
            "beam",
            "h_beam",
            "wall_column",
            "fence_post",
            "joist",
            "crown_molding",
            "post_cap",
            "post_lantern",
            "rod",
            "roof",
            "wall_post",
            "lattice",
            "facade",
            "tube_metal",
            "i_beam",
            "transom",
            "octagonal_column",
            "round_arch",
            "round_fence_post"
        )
    }

    private val path = output.path.resolve("staticdata/architecture_extensions")

    override fun run(writer: DataWriter): CompletableFuture<*> {
        return CompletableFuture.allOf(*(INTEGRATION_BLOCKS.map { block ->
            val blockId = Registries.BLOCK.getId(block)
            val filePath = path.resolve(blockId.toUnderscoreSeparatedString() + ".json")

            val root = JsonObject()
            root.addProperty("only_if_present", "hotm")
            root.addProperty("name", block.translationKey)
            root.addProperty("base_block", blockId.toString())
            root.addProperty("textures", blockId.withPrefix("block/").toString())
            root.addProperty("recipes", "stonecutting")
            root.addProperty("map_color", "deepslate")
            root.add("types_to_generate", JsonArray().apply { TYPES.forEach(::add) })

            DataProvider.writeAsync(writer, root, filePath)
        }.toTypedArray()))
    }

    override fun getName(): String {
        return "Arch-Ex Integration"
    }
}
