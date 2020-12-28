package com.github.hotm

import com.github.hotm.world.gen.HotMBiomes
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.world.biome.Biome
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.introspector.PropertyUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException

class HotMConfig {
    companion object {
        private val FABRIC = FabricLoader.getInstance()

        private val CONFIG_DIR = FABRIC.configDirectory ?: File("config")
        private val CONFIG_FILE = File(CONFIG_DIR, "hotm.yml")

        val CONFIG: HotMConfig

        init {
            val constructor = Constructor(HotMConfig::class.java)
            val propertyUtils = PropertyUtils()
            propertyUtils.isSkipMissingProperties = true
            // constructor's propertyUtils setter also sets propertyUtils for all its typeDescriptions
            constructor.propertyUtils = propertyUtils
            val yaml = Yaml(constructor)

            if (!CONFIG_DIR.exists()) {
                CONFIG_DIR.mkdirs()
            }

            val config: HotMConfig = if (CONFIG_FILE.exists()) {
                try {
                    FileInputStream(CONFIG_FILE).use { yaml.load<HotMConfig>(it) }
                } catch (e: IOException) {
                    HotMLog.log.warn("Error loading HotM config: ", e)
                    HotMConfig()
                }
            } else {
                HotMConfig()
            }

            if (config.necterePortalWorldGenBlacklistBiomes == null) {
                val biomeRegistry = HotMBiomes.builtinBiomeRegistry()
                val biomes = mutableListOf<String>()

                for (biomeId in biomeRegistry.ids) {
                    val biome = biomeRegistry[biomeId]!!
                    if (biome.category == Biome.Category.OCEAN || biome.category == Biome.Category.RIVER) {
                        biomes += biomeId.toString()
                    }
                }

                config.necterePortalWorldGenBlacklistBiomes = biomes
            }

            try {
                FileWriter(CONFIG_FILE).use { it.write(yaml.dumpAsMap(config)) }
            } catch (e: IOException) {
                HotMLog.log.warn("Error saving HotM config: ", e)
            }

            CONFIG = config
        }

        fun init() {
            // Make sure the static initializer has been called
        }
    }

    var necterePortalWorldGenBlacklistBiomes: MutableList<String>? = null

    var forceNectereBiomeSource = false

    var generateMissingPortals = true

    var nectereAuraBaseValue: Int = 64

    var nonNectereAuraBaseValue: Int = 0
}