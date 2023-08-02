package com.github.hotm.mod.item

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.item.ItemUsageContext
import net.minecraft.util.ActionResult

interface InteractionCanceler {
    companion object {
        fun init() {
            UseBlockCallback.EVENT.register { player, _, hand, hitResult ->
                if (!player.isSpectator) {
                    val stack = player.getStackInHand(hand)
                    (stack?.item as? InteractionCanceler)?.let { item ->
                        val usageContext = ItemUsageContext(player, hand, hitResult)
                        if (item.shouldCancelInteraction(usageContext)) {
                            if (player.isCreative) {
                                val count = stack.count
                                val result = stack.useOnBlock(usageContext)
                                stack.count = count
                                result
                            } else {
                                stack.useOnBlock(usageContext)
                            }
                        } else {
                            null
                        }
                    }
                } else {
                    null
                } ?: ActionResult.PASS
            }
        }
    }

    fun shouldCancelInteraction(usageContext: ItemUsageContext): Boolean
}
