package com.github.hotm.misc

import com.github.hotm.HotMConstants
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object HotMLog {
    @JvmStatic
    val log: Logger = LogManager.getLogger(HotMConstants.MOD_ID)
}