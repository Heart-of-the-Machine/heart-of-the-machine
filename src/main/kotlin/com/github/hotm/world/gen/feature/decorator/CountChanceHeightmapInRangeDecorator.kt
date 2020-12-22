package com.github.hotm.world.gen.feature.decorator

import com.mojang.serialization.Codec
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.minecraft.world.gen.decorator.Decorator
import net.minecraft.world.gen.decorator.DecoratorContext
import java.util.*
import java.util.stream.IntStream
import java.util.stream.Stream

class CountChanceHeightmapInRangeDecorator(codec: Codec<CountChanceInRangeDecoratorConfig>) :
    Decorator<CountChanceInRangeDecoratorConfig>(codec) {
    override fun getPositions(
        context: DecoratorContext,
        random: Random,
        config: CountChanceInRangeDecoratorConfig,
        pos: BlockPos
    ): Stream<BlockPos?> {
        return IntStream.range(0, config.count).filter {
            random.nextFloat() < config.chance
        }.mapToObj {
            val x = random.nextInt(16) + pos.x
            val z = random.nextInt(16) + pos.z
            val y = context.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z)
            if (y in config.minHeight until config.maxHeight) BlockPos(x, y, z) else null
        }.filter(Objects::nonNull)
    }
}