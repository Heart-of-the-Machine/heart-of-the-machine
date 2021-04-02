package com.github.hotm

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.spec.Spec
import io.kotest.extensions.allure.AllureTestReporter
import kotlin.reflect.KClass

class TestProjectConfig : AbstractProjectConfig() {
    override fun listeners() = listOf(AllureTestReporter())
    override fun extensions() = listOf(ClassLoaderConstructorExtension)
}

object ClassLoaderConstructorExtension : ConstructorExtension {
    override fun <T : Spec> instantiate(clazz: KClass<T>): Spec? {
        val cl = EmptyClassLoader()
        return cl.loadClass(clazz.qualifiedName).newInstance() as Spec?
    }
}
