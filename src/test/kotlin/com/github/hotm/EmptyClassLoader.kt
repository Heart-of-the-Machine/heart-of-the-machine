package com.github.hotm

import org.objectweb.asm.*
import org.objectweb.asm.commons.GeneratorAdapter
import org.objectweb.asm.commons.Method
import org.objenesis.Objenesis
import org.objenesis.ObjenesisStd
import java.io.File
import java.io.IOException

class EmptyClassLoader : ClassLoader() {
    companion object {
        @JvmStatic
        val OBJENISIS: Objenesis = ObjenesisStd()
    }

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        synchronized(getClassLoadingLock(name)) {
            var c = findLoadedClass(name)

            if (c == null
                && !name.startsWith("java.")
                && !name.startsWith("jdk.")
                && !name.startsWith("sun.")
                && !name.startsWith("io.kotest")
                && !name.startsWith("kotlin")
            ) {
                loadClassBytecode(name)?.let { input ->
                    val pkgDelimiterPos = name.lastIndexOf('.')
                    if (pkgDelimiterPos > 0) {
                        val pkgString = name.substring(0, pkgDelimiterPos)
                        if (getDefinedPackage(pkgString) == null) {
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
//        val printer = TraceClassVisitor(writer, PrintWriter(System.out))

            if (name.startsWith("net.minecraft")) {
                val emptier = EmptyClassVisitor(writer)
                reader.accept(emptier, ClassReader.EXPAND_FRAMES)
            } else {
                reader.accept(writer, ClassReader.EXPAND_FRAMES)
            }

            return writer.toByteArray()
        } catch (e: IOException) {
            System.err.println("Error loading class: $name")
            return null
        }
    }

    class EmptyClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM9, cv) {
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
            return EmptyMethodVisitor(
                access,
                name,
                descriptor,
                mv,
                superType,
                "<clinit>" != name
            )
        }
    }

    class EmptyMethodVisitor(
        private val access: Int,
        private val name: String,
        descriptor: String,
        mv: MethodVisitor,
        superType: Type,
        private val except: Boolean
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

                if (except) {
                    val reType = Type.getType(RuntimeException::class.java)
                    ga.throwException(reType, "Encountered stub method")
                } else {
                    when (method.returnType.sort) {
                        Type.VOID -> {
                            ga.visitInsn(Opcodes.RETURN)
                        }
                        Type.BOOLEAN -> {
                            ga.push(false)
                            ga.returnValue()
                        }
                        Type.CHAR -> {
                            ga.push(20)
                            ga.returnValue()
                        }
                        Type.BYTE -> {
                            ga.push(0)
                            ga.returnValue()
                        }
                        Type.SHORT -> {
                            ga.push(0)
                            ga.returnValue()
                        }
                        Type.INT -> {
                            ga.push(0)
                            ga.returnValue()
                        }
                        Type.FLOAT -> {
                            ga.push(0f)
                            ga.returnValue()
                        }
                        Type.LONG -> {
                            ga.push(0L)
                            ga.returnValue()
                        }
                        Type.DOUBLE -> {
                            ga.push(0.0)
                            ga.returnValue()
                        }
                        Type.ARRAY -> {
                            ga.push(0)
                            ga.newArray(method.returnType.elementType)
                            ga.returnValue()
                        }
                        Type.OBJECT -> {
                            val clType = Type.getType(EmptyClassLoader::class.java)
                            val objType = Type.getType(Objenesis::class.java)
                            ga.getStatic(clType, "OBJENESIS", objType)
                            ga.push(method.returnType)
                            ga.invokeInterface(objType, Method.getMethod("Object newInstance (Class)"))
                            ga.checkCast(method.returnType)
                            ga.returnValue()
                        }
                    }
                }
            }

            ga.endMethod()
        }
    }
}