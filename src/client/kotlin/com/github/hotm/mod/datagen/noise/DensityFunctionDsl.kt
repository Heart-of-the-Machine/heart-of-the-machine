package com.github.hotm.mod.datagen.noise

import java.util.function.Function
import com.github.hotm.mod.Constants.id
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.util.Identifier

interface DensityFunctionDsl {
    companion object {
        private val REGISTRY = SimpleRegistry<Codec<out DensityFunctionDsl>>(
            RegistryKey.ofRegistry(id("density_function_dsl")),
            Lifecycle.experimental()
        )
        private val REGISTRY_CODEC = REGISTRY.codec.dispatch(DensityFunctionDsl::codec, Function.identity())

        val CODEC: Codec<DensityFunctionDsl> = Codec.either(
            Constant.CODEC,
            Codec.either(Reference.CODEC, REGISTRY_CODEC).xmap(
                { it.map(Function.identity(), Function.identity()) },
                {
                    if (it is Reference) {
                        Either.left(it)
                    } else {
                        Either.right(it)
                    }
                }
            )
        ).xmap(
            { it.map(Function.identity(), Function.identity()) },
            {
                if (it is Constant) {
                    Either.left(it)
                } else {
                    Either.right(it)
                }
            }
        )

        fun register(id: String, codec: Codec<out DensityFunctionDsl>) {
            Registry.register(REGISTRY, id, codec)
        }
    }

    val codec: Codec<out DensityFunctionDsl>

    /**
     * Creates a density function that is the multiplication of these two density functions.
     */
    infix operator fun times(other: DensityFunctionDsl): DensityFunctionDsl {
        return if (other is Constant && this is Constant) {
            Constant(this.value * other.value)
        } else {
            Multiply(this, other)
        }
    }

    /**
     * Creates a density function that is the addition of these two density functions.
     */
    infix operator fun plus(other: DensityFunctionDsl): DensityFunctionDsl {
        return if (other is Constant && this is Constant) {
            Constant(this.value + other.value)
        } else {
            Add(this, other)
        }
    }

    /**
     * Creates a density function that is the subtraction of the other from this one.
     */
    infix operator fun minus(other: DensityFunctionDsl): DensityFunctionDsl {
        return if (other is Constant) {
            if (this is Constant) {
                Constant(this.value - other.value)
            } else {
                this + Constant(-other.value)
            }
        } else {
            this + (-1.0).df * other
        }
    }

    /**
     * Creates a squeeze density function out of this one.
     */
    fun squeeze(): DensityFunctionDsl = Squeeze(this)

    /**
     * Creates an interpolated density function out of this one.
     */
    fun interpolated(): DensityFunctionDsl = Interpolated(this)

    /**
     * Creates a blend-density density function out of this one.
     */
    fun blendDensity(): DensityFunctionDsl = BlendDensity(this)
}

data class Constant(val value: Double) : DensityFunctionDsl {
    companion object {
        val CODEC: Codec<Constant> = Codec.DOUBLE.xmap(::Constant, Constant::value)
    }

    override val codec: Codec<out DensityFunctionDsl>
        get() = CODEC
}

/**
 * Creates a density function with a constant value.
 */
val Double.df: DensityFunctionDsl
    get() = Constant(this)

/**
 * Creates a density function with a constant value.
 */
val Int.df: DensityFunctionDsl
    get() = Constant(this.toDouble())

/**
 * Density function, constant: 0.
 */
val zero: DensityFunctionDsl = Constant(0.0)

data class Reference(val ref: Identifier) : DensityFunctionDsl {
    companion object {
        val CODEC: Codec<Reference> = Identifier.CODEC.xmap(::Reference, Reference::ref)
    }

    override val codec: Codec<out DensityFunctionDsl>
        get() = CODEC
}

/**
 * Creates a reference to a density function specified elsewhere.
 */
val Identifier.df: DensityFunctionDsl
    get() = Reference(this)

/**
 * Creates a reference to a density function specified elsewhere.
 */
val String.df: DensityFunctionDsl
    get() = Reference(Identifier(this))

private fun <T : DensityFunctionDsl> twoInputCodec(
    getArg1: (T) -> DensityFunctionDsl, getArg2: (T) -> DensityFunctionDsl,
    constr: (DensityFunctionDsl, DensityFunctionDsl) -> T
): Codec<T> {
    return RecordCodecBuilder.create { instance ->
        instance.group(
            DensityFunctionDsl.CODEC.fieldOf("argument1").forGetter(getArg1),
            DensityFunctionDsl.CODEC.fieldOf("argument2").forGetter(getArg2)
        ).apply(instance, constr)
    }
}

data class Multiply(val arg1: DensityFunctionDsl, val arg2: DensityFunctionDsl) : DensityFunctionDsl {
    companion object {
        val CODEC = twoInputCodec(Multiply::arg1, Multiply::arg2, ::Multiply)

        init {
            DensityFunctionDsl.register("mul", CODEC)
        }
    }

    override val codec: Codec<out DensityFunctionDsl>
        get() = CODEC
}

data class Add(val arg1: DensityFunctionDsl, val arg2: DensityFunctionDsl) : DensityFunctionDsl {
    companion object {
        val CODEC = twoInputCodec(Add::arg1, Add::arg2, ::Add)

        init {
            DensityFunctionDsl.register("add", CODEC)
        }
    }

    override val codec: Codec<out DensityFunctionDsl>
        get() = CODEC
}

private fun <T : DensityFunctionDsl> oneInputCodec(
    getArg: (T) -> DensityFunctionDsl, constr: (DensityFunctionDsl) -> T
): Codec<T> {
    return RecordCodecBuilder.create { instance ->
        instance.group(
            DensityFunctionDsl.CODEC.fieldOf("argument").forGetter(getArg)
        ).apply(instance, constr)
    }
}

data class Squeeze(val arg: DensityFunctionDsl) : DensityFunctionDsl {
    companion object {
        val CODEC = oneInputCodec(Squeeze::arg, ::Squeeze)

        init {
            DensityFunctionDsl.register("squeeze", CODEC)
        }
    }

    override val codec: Codec<out DensityFunctionDsl>
        get() = CODEC
}

data class Interpolated(val arg: DensityFunctionDsl) : DensityFunctionDsl {
    companion object {
        val CODEC = oneInputCodec(Interpolated::arg, ::Interpolated)

        init {
            DensityFunctionDsl.register("interpolated", CODEC)
        }
    }

    override val codec: Codec<out DensityFunctionDsl>
        get() = CODEC
}

data class BlendDensity(val arg: DensityFunctionDsl) : DensityFunctionDsl {
    companion object {
        val CODEC = oneInputCodec(BlendDensity::arg, ::BlendDensity)

        init {
            DensityFunctionDsl.register("blend_density", CODEC)
        }
    }

    override val codec: Codec<out DensityFunctionDsl>
        get() = CODEC
}

data class YClampedGradient(
    val fromY: Int,
    val toY: Int,
    val fromValue: Double,
    val toValue: Double
) : DensityFunctionDsl {
    companion object {
        val CODEC: Codec<YClampedGradient> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("from_y").forGetter(YClampedGradient::fromY),
                Codec.INT.fieldOf("to_y").forGetter(YClampedGradient::toY),
                Codec.DOUBLE.fieldOf("from_value").forGetter(YClampedGradient::fromValue),
                Codec.DOUBLE.fieldOf("to_value").forGetter(YClampedGradient::toValue)
            ).apply(instance, ::YClampedGradient)
        }

        init {
            DensityFunctionDsl.register("y_clamped_gradient", CODEC)
        }
    }

    override val codec: Codec<out DensityFunctionDsl>
        get() = CODEC
}

fun yGradient(fromY: Int, toY: Int, fromValue: Double, toValue: Double): DensityFunctionDsl =
    YClampedGradient(fromY, toY, fromValue, toValue)

data class ShiftedNoise(
    val noise: Identifier,
    val xzScale: Double,
    val yScale: Double,
    val shiftX: DensityFunctionDsl,
    val shiftY: DensityFunctionDsl,
    val shiftZ: DensityFunctionDsl
) : DensityFunctionDsl {
    companion object {
        val CODEC: Codec<ShiftedNoise> = RecordCodecBuilder.create { instance ->
            instance.group(
                Identifier.CODEC.fieldOf("noise").forGetter(ShiftedNoise::noise),
                Codec.DOUBLE.fieldOf("xz_scale").forGetter(ShiftedNoise::xzScale),
                Codec.DOUBLE.fieldOf("y_scale").forGetter(ShiftedNoise::yScale),
                DensityFunctionDsl.CODEC.fieldOf("shift_x").forGetter(ShiftedNoise::shiftX),
                DensityFunctionDsl.CODEC.fieldOf("shift_y").forGetter(ShiftedNoise::shiftY),
                DensityFunctionDsl.CODEC.fieldOf("shift_z").forGetter(ShiftedNoise::shiftZ)
            ).apply(instance, ::ShiftedNoise)
        }

        init {
            DensityFunctionDsl.register("shifted_noise", CODEC)
        }
    }

    override val codec: Codec<out DensityFunctionDsl>
        get() = CODEC
}

fun shiftedNoise(
    noise: Identifier,
    xzScale: Double = 0.0,
    yScale: Double = 0.0,
    shiftX: DensityFunctionDsl = zero,
    shiftY: DensityFunctionDsl = zero,
    shiftZ: DensityFunctionDsl = zero
): DensityFunctionDsl = ShiftedNoise(noise, xzScale, yScale, shiftX, shiftY, shiftZ)
