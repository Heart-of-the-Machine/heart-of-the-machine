package com.github.hotm.gen.feature.decorator

import com.mojang.serialization.Codec
import it.unimi.dsi.fastutil.ints.IntArrayList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.decorator.Decorator
import java.util.*
import java.util.stream.IntStream
import java.util.stream.Stream

/**
 * Picks an X and Z location within the given chunk and then picks one of the many potential surface Y levels within the
 * requested range.
 */
class CountChanceSurfaceInRangeDecorator(codec: Codec<CountChanceSurfaceInRangeDecoratorConfig>) :
    Decorator<CountChanceSurfaceInRangeDecoratorConfig>(codec) {
    private val levels: ThreadLocal<IntArrayList> = ThreadLocal.withInitial { IntArrayList() }

    override fun getPositions(
        world: WorldAccess,
        generator: ChunkGenerator,
        random: Random,
        config: CountChanceSurfaceInRangeDecoratorConfig,
        pos: BlockPos
    ): Stream<BlockPos?> {
        return IntStream.range(0, config.count).filter {
            random.nextFloat() < config.chance
        }.mapToObj {
            val x = random.nextInt(16) + pos.x
            val z = random.nextInt(16) + pos.z

            val mutable = BlockPos.Mutable(x, config.minHeight, z)
            val ls = levels.get()
            ls.clear()
            for (y in config.minHeight until config.maxHeight) {
                mutable.y = y
                if (world.isAir(mutable) && !world.isAir(mutable.down())) {
                    ls.add(y)
                }
            }

            if (ls.isEmpty) {
                null
            } else {
                BlockPos(x, ls.getInt(random.nextInt(ls.size)), z)
            }
        }.filter(Objects::nonNull)
    }
}