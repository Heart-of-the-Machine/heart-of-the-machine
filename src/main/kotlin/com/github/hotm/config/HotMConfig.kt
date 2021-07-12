package com.github.hotm.config

import com.github.hotm.misc.HotMLog
import net.fabricmc.loader.api.FabricLoader
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.introspector.PropertyUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException

class HotMConfig {
    var forceNectereBiomeSource = false

    var generateMissingPortals = true

    companion object {
        private val FABRIC = FabricLoader.getInstance()

        private val CONFIG_DIR = FABRIC.configDirectory ?: File("config")
        private val CONFIG_FILE = File(CONFIG_DIR, "hotm.yml")

        val CONFIG by lazy {
            HotMLog.log.info("Loading HotM config...")

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
                    FileInputStream(CONFIG_FILE).use { yaml.load(it) }
                } catch (e: IOException) {
                    HotMLog.log.warn("Error loading HotM config: ", e)
                    HotMConfig()
                }
            } else {
                HotMConfig()
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