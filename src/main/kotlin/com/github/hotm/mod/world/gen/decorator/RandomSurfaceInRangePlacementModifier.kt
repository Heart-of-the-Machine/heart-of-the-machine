package com.github.hotm.mod.world.gen.decorator

import java.util.stream.Stream
import it.unimi.dsi.fastutil.ints.IntArrayList
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.math.BlockPos
import net.minecraft.util.random.RandomGenerator
import net.minecraft.world.gen.YOffset
import net.minecraft.world.gen.decorator.DecoratorContext
import net.minecraft.world.gen.decorator.PlacementModifierType
import net.minecraft.world.gen.feature.PlacementModifier

class RandomSurfaceInRangePlacementModifier(val minInclusive: YOffset, val maxInclusive: YOffset) :
    PlacementModifier() {
    companion object {
        val MODIFIER_CODEC: Codec<RandomSurfaceInRangePlacementModifier> = RecordCodecBuilder.create { instance ->
            instance.group(
                YOffset.OFFSET_CODEC.fieldOf("min_inclusive").forGetter { it.minInclusive },
                YOffset.OFFSET_CODEC.fieldOf("max_inclusive").forGetter { it.maxInclusive }
            ).apply(instance, ::RandomSurfaceInRangePlacementModifier)
        }

        private val levels: ThreadLocal<IntArrayList> = ThreadLocal.withInitial { IntArrayList() }
    }

    override fun getPositions(context: DecoratorContext, random: RandomGenerator, pos: BlockPos): Stream<BlockPos> {
        val minHeight = minInclusive.getY(context)
        val maxHeight = maxInclusive.getY(context)

        val mutable = pos.mutableCopy()
        val ls = levels.get()
        ls.clear()
        for (y in minHeight..maxHeight) {
            mutable.y = y
            if (context.getBlockState(mutable).isAir && !context.getBlockState(mutable.down()).isAir) {
                ls.add(y)
            }
        }

        if (ls.isEmpty) {
            return Stream.empty()
        } else {
            return Stream.of(BlockPos(pos.x, ls.getInt(random.nextInt(ls.size)), pos.z))
        }
    }

    override fun getType(): PlacementModifierType<*> = HotMPlacementModifiers.RANDOM_SURFACE_IN_RANGE
}
