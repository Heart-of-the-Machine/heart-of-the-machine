package com.github.hotm

import io.kotest.core.spec.style.FunSpec

interface RunInSpec {
    fun run(spec: FunSpec)
}

inline fun FunSpec.runInMockCL(crossinline toRun: FunSpec.() -> Unit) {
    val cl = MockClassLoader.instance
    val runnable = object : RunInSpec {
        override fun run(spec: FunSpec) {
            spec.toRun()
        }
    }
    val runnableClass = runnable.javaClass
    val alteredClass = cl.loadClass(runnableClass.name)
    val runnable2 = MockClassLoader.OBJENISIS.newInstance(alteredClass)
    val runMethod = alteredClass.getMethod("run", FunSpec::class.java)
    runMethod.isAccessible = true
    runMethod.invoke(runnable2, this)
}
