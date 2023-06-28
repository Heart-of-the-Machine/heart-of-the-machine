package com.github.hotm.mod.world.biome

import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import com.github.hotm.mod.Constants
import com.github.hotm.mod.HotMLog
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents
import org.quiltmc.qsl.resource.loader.api.ResourceLoader
import org.quiltmc.qsl.resource.loader.api.reloader.SimpleResourceReloader
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.profiler.Profiler
import net.minecraft.world.World
import net.minecraft.world.biome.Biome

data class NecterePortalData(val coordinateFactor: Double, val targetWorld: RegistryKey<World>) {
    companion object {
        val CODEC: Codec<NecterePortalData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.DOUBLE.fieldOf("coordinate_factor").forGetter(NecterePortalData::coordinateFactor),
                RegistryKey.codec(RegistryKeys.WORLD).fieldOf("target_world").forGetter(NecterePortalData::targetWorld)
            ).apply(instance, ::NecterePortalData)
        }

        val BIOMES_BY_ID = mutableMapOf<RegistryKey<Biome>, Holder>()
        val BIOMES_BY_WORLD: Multimap<RegistryKey<World>, Holder> = HashMultimap.create<RegistryKey<World>, Holder>()

        fun init() {
            ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(Loader)

            ServerLifecycleEvents.STOPPED.register {
                BIOMES_BY_ID.clear()
                BIOMES_BY_WORLD.clear()
            }
        }

        inline fun <T> ifData(optionalBiome: Optional<RegistryKey<Biome>>, then: (Holder) -> T): T? {
            if (optionalBiome.isEmpty) return null
            val biome = optionalBiome.get()

            return if (BIOMES_BY_ID.containsKey(biome)) then(BIOMES_BY_ID[biome]!!) else null
        }
    }

    data class Holder(val biome: RegistryKey<Biome>, val data: NecterePortalData)

    private object Loader : SimpleResourceReloader<LoadedData> {
        val ID = Constants.id("nectere_portal_data")

        override fun getQuiltId(): Identifier = ID

        override fun load(
            manager: ResourceManager, profiler: Profiler, executor: Executor
        ): CompletableFuture<LoadedData> {
            return CompletableFuture.supplyAsync({
                val byId = mutableMapOf<RegistryKey<Biome>, Holder>()
                val byWorld = HashMultimap.create<RegistryKey<World>, Holder>()

                HotMLog.LOG.info("[HotM] Loading Nectere Biome Data...")

                val path = ID.namespace + "/" + ID.path
                val resources = manager.findResources(path) { it.path.endsWith(".json") }
                for ((loc, res) in resources) {
                    val id =
                        Identifier(loc.namespace, loc.path.substring(path.length + 1, loc.path.length - ".json".length))
                    res.openBufferedReader().use { reader ->
                        val data = CODEC.decode(JsonOps.INSTANCE, JsonHelper.deserialize(reader))
                            .getOrThrow(false, HotMLog.LOG::error).first
                        val biomeKey = RegistryKey.of(RegistryKeys.BIOME, id)
                        val holder = Holder(biomeKey, data)

                        byId[biomeKey] = holder

                        data.targetWorld?.let { world -> byWorld.put(world, holder) }
                    }
                }

                return@supplyAsync LoadedData(byId, byWorld)
            }, executor)
        }

        override fun apply(
            data: LoadedData, manager: ResourceManager, profiler: Profiler, executor: Executor
        ): CompletableFuture<Void> {
            return CompletableFuture.runAsync({
                BIOMES_BY_ID.clear()
                BIOMES_BY_WORLD.clear()

                BIOMES_BY_ID.putAll(data.byId)
                BIOMES_BY_WORLD.putAll(data.byWorld)
            }, executor)
        }
    }

    private data class LoadedData(
        val byId: Map<RegistryKey<Biome>, Holder>, val byWorld: Multimap<RegistryKey<World>, Holder>
    )
}
