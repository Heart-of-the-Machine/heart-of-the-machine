package com.github.hotm.world.gen.surfacebuilder

import com.github.hotm.HotMConstants
import com.github.hotm.blocks.HotMBlocks
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig

object HotMSurfaceBuilders {
    val GRASS_BLOCK by lazy { HotMBlocks.PLASSEIN_GRASS }
    val RUSTED_SURFACE_BLOCK by lazy { HotMBlocks.RUSTED_MACHINE_CASING }
    val SAND_BLOCK by lazy { HotMBlocks.NULL_SAND }
    val SURFACE_BLOCK by lazy { HotMBlocks.SURFACE_MACHINE_CASING }

    private val GRASS_STATE by lazy { GRASS_BLOCK.defaultState }
    private val RUSTED_SURFACE_STATE by lazy { RUSTED_SURFACE_BLOCK.defaultState }
    private val SAND_STATE by lazy { SAND_BLOCK.defaultState }
    private val SURFACE_STATE by lazy { SURFACE_BLOCK.defaultState }

    lateinit var GRASS_CONFIG: NectereSurfaceConfig
    lateinit var WASTELAND_CONFIG: NectereSurfaceConfig

    lateinit var DEFAULT: NectereSurfaceBuilder
    lateinit var PARTIAL: NecterePartialSurfaceBuilder

    fun register() {
        GRASS_CONFIG = NectereSurfaceConfig(GRASS_STATE, SURFACE_STATE, SAND_STATE)
        WASTELAND_CONFIG = NectereSurfaceConfig(RUSTED_SURFACE_STATE, SURFACE_STATE, SAND_STATE)

        DEFAULT = register("default", NectereSurfaceBuilder(NectereSurfaceConfig.CODEC))
        PARTIAL = register("partial", NecterePartialSurfaceBuilder(NectereSurfaceConfig.CODEC))

        HotMConfiguredSurfaceBuilders.register()
    }

    private fun <C : SurfaceConfig?, F : SurfaceBuilder<C>> register(string: String, surfaceBuilder: F): F {
        return Registry.register(Registry.SURFACE_BUILDER, HotMConstants.identifier(string), surfaceBuilder)
    }
}