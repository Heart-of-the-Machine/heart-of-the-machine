package com.github.hotm.gen.feature.decorator

import com.github.hotm.gen.HotMDimensions
import com.github.hotm.util.WorldUtils
import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.WorldAccess
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.decorator.Decorator
import net.minecraft.world.gen.decorator.NopeDecoratorConfig
import java.util.*
import java.util.stream.Stream

class NecterePortalDecorator(codec: Codec<NopeDecoratorConfig>) : Decorator<NopeDecoratorConfig>(codec) {
    override fun getPositions(
        world: WorldAccess,
        generator: ChunkGenerator,
        random: Random,
        config: NopeDecoratorConfig,
        pos: BlockPos
    ): Stream<BlockPos> {
        val serverWorld = WorldUtils.getServerWorld(world)
        return if (serverWorld != null) {
            val nectereWorld = HotMDimensions.getNectereWorld(serverWorld.server)
            HotMDimensions.getNonNecterePortalCoords(
                world,
                serverWorld.registryKey,
                ChunkPos(pos),
                nectereWorld,
                random
            )
        } else {
            Stream.empty<BlockPos>()
        }
    }
}