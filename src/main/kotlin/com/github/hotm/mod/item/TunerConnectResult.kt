package com.github.hotm.mod.item

import com.github.hotm.mod.Constants
import net.minecraft.registry.RegistryKey
import net.minecraft.text.Text
import net.minecraft.world.World

sealed interface TunerConnectResult {
    val successful: Boolean
    val msg: Text

    object Success : TunerConnectResult {
        override val successful: Boolean
            get() = true
        override val msg: Text
            get() = Constants.msg("node_tuner.success")
    }

    data class WrongDimension(val dim1: RegistryKey<World>, val dim2: RegistryKey<World>) : TunerConnectResult {
        override val successful: Boolean
            get() = false
        override val msg: Text
            get() = Constants.msg("node_tuner.wrong_dimension", dim1.value, dim2.value)
    }

    data class TooFar(val distance: Float, val maxDistance: Float): TunerConnectResult {
        override val successful: Boolean
            get() = false
        override val msg: Text
            get() = Constants.msg("node_tuner.too_far", maxDistance, distance)
    }

    object Blocked : TunerConnectResult {
        override val successful: Boolean
            get() = false
        override val msg: Text
            get() = Constants.msg("node_tuner.blocked")
    }

    object DependencyLoop : TunerConnectResult {
        override val successful: Boolean
            get() = false
        override val msg: Text
            get() = Constants.msg("node_tuner.dependency_loop")
    }
}
