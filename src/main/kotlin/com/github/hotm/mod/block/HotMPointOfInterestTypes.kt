package com.github.hotm.mod.block

import com.github.hotm.mod.Constants.id
import com.github.hotm.mod.mixin.api.HotMMixinHelper
import com.google.common.collect.ImmutableSet
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys

object HotMPointOfInterestTypes {
    val NECTERE_PORTAL = RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, id("nectere_portal"))

    fun init() {
        HotMMixinHelper.registerPointOfInterest(
            Registries.POINT_OF_INTEREST_TYPE,
            NECTERE_PORTAL,
            ImmutableSet.copyOf(HotMBlocks.NECTERE_PORTAL.stateManager.states),
            0,
            1
        )
    }
}
