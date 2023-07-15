package com.github.hotm.mod.world.gen.feature.tree

import com.github.hotm.mod.util.GeometryUtils
import com.github.hotm.mod.world.gen.feature.HotMFeatures
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.int_provider.IntProvider
import net.minecraft.util.random.RandomGenerator
import net.minecraft.world.TestableWorld
import net.minecraft.world.gen.feature.TreeFeatureConfig
import net.minecraft.world.gen.foliage.FoliagePlacer
import net.minecraft.world.gen.foliage.FoliagePlacerType

class SolarArrayFoliagePlacer(radius: IntProvider, offset: IntProvider, val height: IntProvider) :
    FoliagePlacer(radius, offset) {
    companion object {
        val CODEC: Codec<SolarArrayFoliagePlacer> = RecordCodecBuilder.create { instance ->
            fillFoliagePlacerFields(instance)
                .and(IntProvider.create(1, 512).fieldOf("height").forGetter(SolarArrayFoliagePlacer::height))
                .apply(instance, ::SolarArrayFoliagePlacer)
        }
    }

    override fun getType(): FoliagePlacerType<*> = HotMFeatures.SOLAR_ARRAY_FOLIAGE_PLACER

    override fun method_23448(
        world: TestableWorld, c_pwcqvmho: C_pwcqvmho, random: RandomGenerator, treeFeatureConfig: TreeFeatureConfig,
        trunkHeight: Int, treeNode: TreeNode, foliageHeight: Int, radius: Int, offset: Int
    ) {
        val nodeCenter = treeNode.center
        val fullRadius = treeNode.foliageRadius + radius
        val ellipsoid = Vec3d(fullRadius.toDouble(), foliageHeight.toDouble(), fullRadius.toDouble())
        val includeCenter = Vec3d.ofCenter(nodeCenter.down(foliageHeight / 2))
        val excludeCenter = Vec3d.ofCenter(nodeCenter.down(foliageHeight))
        val mutable = BlockPos.Mutable()

        for (y in -foliageHeight..(foliageHeight / 2 + 1)) {
            for (z in -fullRadius..fullRadius) {
                for (x in -fullRadius..fullRadius) {
                    mutable.set(nodeCenter)
                    mutable.move(x, y, z)
                    val vec3d = Vec3d.ofCenter(mutable)
                    if (GeometryUtils.inEllipsoid(includeCenter, ellipsoid, vec3d)
                        && !GeometryUtils.inEllipsoid(excludeCenter, ellipsoid, vec3d)
                    ) {
                        placeFoliageBlock(world, c_pwcqvmho, random, treeFeatureConfig, mutable)
                    }
                }
            }
        }
    }

    override fun getRandomHeight(random: RandomGenerator, trunkHeight: Int, config: TreeFeatureConfig): Int {
        return height.get(random)
    }

    override fun isInvalidForLeaves(
        random: RandomGenerator, dx: Int, y: Int, dz: Int, radius: Int, giantTrunk: Boolean
    ): Boolean = false
}
