package com.github.hotm.config

import com.github.hotm.HotMLog
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.world.biome.Biome
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.introspector.PropertyUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException

class HotMBiomesConfig {
    var necterePortalDenyBiomes: MutableList<String>? = null

    companion object {
        private val FABRIC = FabricLoader.getInstance()

        private val CONFIG_DIR = FABRIC.configDirectory ?: File("config")
        private val CONFIG_FILE = File(CONFIG_DIR, "hotm.biomes.yml")

        val CONFIG by lazy {
            HotMLog.log.info("Loading HotM biome config...")

            val constructor = Constructor(HotMBiomesConfig::class.java)
            val propertyUtils = PropertyUtils()
            propertyUtils.isSkipMissingProperties = true
            // constructor's propertyUtils setter also sets propertyUtils for all its typeDescriptions
            constructor.propertyUtils = propertyUtils
            val yaml = Yaml(constructor)

            if (!CONFIG_DIR.exists()) {
                CONFIG_DIR.mkdirs()
            }

            val config: HotMBiomesConfig = if (CONFIG_FILE.exists()) {
                try {
                    FileInputStream(CONFIG_FILE).use { yaml.load(it) }
                } catch (e: IOException) {
                    HotMLog.log.warn("Error loading HotM config: ", e)
                    HotMBiomesConfig()
                }
            } else {
                HotMBiomesConfig()
            }

            if (config.necterePortalDenyBiomes == null) {
                val biomeRegistry = BuiltinRegistries.BIOME
                val biomes = mutableListOf<String>()

                for (biomeId in biomeRegistry.ids) {
                    val biome = biomeRegistry[biomeId]!!
                    if (biome.category == Biome.Category.OCEAN || biome.category == Biome.Category.RIVER) {
                        biomes += biomeId.toString()
                    }
                }

                config.necterePortalDenyBiomes = biomes
            }

            try {
                FileWriter(CONFIG_FILE).use { it.write(yaml.dumpAsMap(config)) }
            } catch (e: IOException) {
                HotMLog.log.warn("Error saving HotM config: ", e)
            }

            config
        }
    }
}