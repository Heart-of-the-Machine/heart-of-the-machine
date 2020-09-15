package com.github.hotm.gen

import com.github.hotm.HotMBlocks
import com.github.hotm.HotMConstants
import com.github.hotm.gen.surfacebuilder.NecterePartialSurfaceBuilder
import com.github.hotm.gen.surfacebuilder.NectereSurfaceBuilder
import com.github.hotm.gen.surfacebuilder.NectereSurfaceConfig
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig

object HotMSurfaceBuilders {
    val GRASS_BLOCK = HotMBlocks.PLASSEIN_GRASS
    val RUSTED_SURFACE_BLOCK = HotMBlocks.RUSTED_MACHINE_CASING
    val SAND_BLOCK = HotMBlocks.NULL_SAND
    val SURFACE_BLOCK = HotMBlocks.SURFACE_MACHINE_CASING

    private val GRASS_STATE = GRASS_BLOCK.defaultState
    private val RUSTED_SURFACE_STATE = RUSTED_SURFACE_BLOCK.defaultState
    private val SAND_STATE = SAND_BLOCK.defaultState
    private val SURFACE_STATE = SURFACE_BLOCK.defaultState

    val GRASS_CONFIG = NectereSurfaceConfig(GRASS_STATE, SURFACE_STATE, SAND_STATE)
    val WASTELAND_CONFIG = NectereSurfaceConfig(RUSTED_SURFACE_STATE, SURFACE_STATE, SAND_STATE)

    val DEFAULT = register("default", NectereSurfaceBuilder(NectereSurfaceConfig.CODEC))
    val PARTIAL = register("partial", NecterePartialSurfaceBuilder(NectereSurfaceConfig.CODEC))

    private fun <C : SurfaceConfig?, F : SurfaceBuilder<C>> register(string: String, surfaceBuilder: F): F {
        return Registry.register(Registry.SURFACE_BUILDER, HotMConstants.identifier(string), surfaceBuilder)
    }
}