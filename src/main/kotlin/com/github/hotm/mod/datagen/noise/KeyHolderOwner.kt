package com.github.hotm.mod.datagen.noise

import net.minecraft.registry.HolderLookup.RegistryLookup
import net.minecraft.registry.HolderOwner
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey

class KeyHolderOwner<T> private constructor(private val registryKey: RegistryKey<out Registry<out T>>) :
    HolderOwner<T> {
    companion object {
        private val INSTANCES = mutableMapOf<RegistryKey<*>, KeyHolderOwner<*>>()

        @Suppress("unchecked_cast")
        fun <T> get(key: RegistryKey<out Registry<out T>>): KeyHolderOwner<T> {
            return INSTANCES.computeIfAbsent(key) { KeyHolderOwner(key) } as KeyHolderOwner<T>
        }
    }

    override fun isSame(owner: HolderOwner<T>): Boolean {
        return super.isSame(owner) || if (owner is RegistryLookup<T>) owner.key === registryKey else false
    }
}
