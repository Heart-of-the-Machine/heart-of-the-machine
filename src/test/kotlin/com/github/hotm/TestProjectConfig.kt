package com.github.hotm

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.allure.AllureTestReporter

class TestProjectConfig : AbstractProjectConfig() {
    override fun listeners() = listOf(AllureTestReporter())
}