package com.github.hotm

import org.objectweb.asm.*
import org.objectweb.asm.commons.GeneratorAdapter
import org.objectweb.asm.commons.Method
import org.objenesis.Objenesis
import org.objenesis.ObjenesisStd
import java.io.File
import java.io.IOException

class MockClassLoader : ClassLoader() {
    companion object {
        @JvmStatic
        val OBJENISIS: Objenesis = ObjenesisStd()

        val instance by lazy { MockClassLoader() }

        private val ALTERED_LOAD = listOf("net.minecraft.", "com.github.hotm.")
        private val ALTERED_EXEMPT = listOf("com.github.hotm.", "net.minecraft.util.")
    }

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        synchronized(getClassLoadingLock(name)) {
            var c = findLoadedClass(name)

            if (c == null && ALTERED_LOAD.find { name.startsWith(it) } != null) {
                loadClassBytecode(name)?.let { input ->
                    val pkgDelimiterPos = name.lastIndexOf('.')
                    if (pkgDelimiterPos > 0) {
                        val pkgString = name.substring(0, pkgDelimiterPos)
                        if (getPackage(pkgString) == null) {
                            definePackage(pkgString, null, null, null, null, null, null, null)
                        }
                    }

                    c = defineClass(name, input, 0, input.size)
                }
            }

            if (c == null) {
                c = parent.loadClass(name)
            }

            if (resolve) {
                resolveClass(c)
            }

            return c
        }
    }

    private fun loadClassBytecode(name: String): ByteArray? {
        try {
            val reader =
                ClassReader(javaClass.classLoader.getResourceAsStream(name.replace('.', File.separatorChar) + ".class"))
            val writer = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS)

            if (ALTERED_EXEMPT.find { name.startsWith(it) } != null) {
                reader.accept(writer, ClassReader.EXPAND_FRAMES)
            } else {
                val emptier = MockClassVisitor(writer)
                reader.accept(emptier, ClassReader.EXPAND_FRAMES)
            }

            return writer.toByteArray()
        } catch (e: IOException) {
            System.err.println("Error loading class: $name")
            e.printStackTrace()
            return null
        }
    }

    class MockClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM9, cv) {
        private var superType = Type.getType(Object::class.java)

        override fun visit(
            version: Int,
            access: Int,
            name: String?,
            signature: String?,
            superName: String?,
            interfaces: Array<out String>?
        ) {
            super.visit(version, access, name, signature, superName, interfaces)
            superName?.let {
                superType = Type.getObjectType(it)
            }
        }

        override fun visitMethod(
            access: Int,
            name: String,
            descriptor: String,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor {
            val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
            return MockMethodVisitor(
                access,
                name,
                descriptor,
                mv
            )
        }
    }

    class MockMethodVisitor(
        private val access: Int,
        private val name: String,
        descriptor: String,
        mv: MethodVisitor
    ) : MethodVisitor(Opcodes.ASM9, if (name == "<init>") mv else null) {
        private val method = Method(name, descriptor)
        private val ga = GeneratorAdapter(access, method, mv)
        override fun visitParameter(name: String, access: Int) {
            ga.visitParameter(name, access)
        }

        override fun visitAnnotationDefault(): AnnotationVisitor {
            return ga.visitAnnotationDefault()
        }

        override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
            return ga.visitAnnotation(descriptor, visible)
        }

        override fun visitTypeAnnotation(
            typeRef: Int,
            typePath: TypePath,
            descriptor: String,
            visible: Boolean
        ): AnnotationVisitor {
            return ga.visitTypeAnnotation(typeRef, typePath, descriptor, visible)
        }

        override fun visitAnnotableParameterCount(parameterCount: Int, visible: Boolean) {
            ga.visitAnnotableParameterCount(parameterCount, visible)
        }

        override fun visitParameterAnnotation(
            parameter: Int,
            descriptor: String,
            visible: Boolean
        ): AnnotationVisitor {
            return ga.visitParameterAnnotation(parameter, descriptor, visible)
        }

        override fun visitAttribute(attribute: Attribute) {
            ga.visitAttribute(attribute)
        }

        override fun visitCode() {
            ga.visitCode()
        }

        override fun visitMethodInsn(
            opcode: Int,
            owner: String?,
            name: String?,
            descriptor: String?,
            isInterface: Boolean
        ) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
            if (name == "<init>") {
                mv = null
            }
        }

        override fun visitEnd() {
            if (access and Opcodes.ACC_ABSTRACT == 0) {
                ga.mark()

                if (name != "<clinit>") {
                    val reType = Type.getType(RuntimeException::class.java)
                    ga.throwException(reType, "Encountered stub method")
                } else {
                    ga.visitInsn(Opcodes.RETURN)
                }
            }

            ga.endMethod()
        }
    }
}