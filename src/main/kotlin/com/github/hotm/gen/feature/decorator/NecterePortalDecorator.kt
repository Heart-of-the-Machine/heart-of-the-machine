package com.github.hotm.gen.feature.decorator

import com.github.hotm.HotMConfig
import com.github.hotm.gen.HotMBiomes
import com.github.hotm.gen.HotMDimensions
import com.github.hotm.gen.feature.NecterePortalGen
import com.github.hotm.util.WorldUtils
import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.gen.decorator.Decorator
import net.minecraft.world.gen.decorator.DecoratorContext
import net.minecraft.world.gen.decorator.NopeDecoratorConfig
import java.util.*
import java.util.stream.Stream

class NecterePortalDecorator(codec: Codec<NopeDecoratorConfig>) : Decorator<NopeDecoratorConfig>(codec) {
    override fun getPositions(
        context: DecoratorContext,
        random: Random,
        config: NopeDecoratorConfig,
        pos: BlockPos
    ): Stream<BlockPos> {
        // We need both a ChunkRegion and a ServerWorld
        val world = WorldUtils.getWorld(context)
        val serverWorld = WorldUtils.getServerWorld(context)

        return if (serverWorld != null && serverWorld.registryKey != HotMDimensions.NECTERE_KEY) {
            val nectereWorld = HotMDimensions.getNectereWorld(serverWorld.server)
            HotMDimensions.getNonNecterePortalCoords(
                serverWorld.registryManager,
                serverWorld.registryKey,
                ChunkPos(pos),
                { resX, resZ -> NecterePortalGen.getPortalStructureY(context, resX, resZ, random) },
                nectereWorld
            ).flatMap { structurePos ->
                // Make sure the portal is in an enabled biome and not in a Nectere biome.
                val portalPos = NecterePortalGen.portalPos(structurePos)
                val biome = world.method_31081(portalPos).orElse(null)

                if (biome != null && !HotMConfig.CONFIG.necterePortalWorldGenBlacklistBiomes!!.contains(biome.value.toString())
                    && !HotMBiomes.biomeData().containsKey(biome)
                ) {
                    Stream.of(structurePos)
                } else {
                    Stream.empty()
                }
            }
        } else {
            Stream.empty()
        }
    }
}