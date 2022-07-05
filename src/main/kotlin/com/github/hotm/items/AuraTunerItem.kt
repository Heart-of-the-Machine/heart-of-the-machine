package com.github.hotm.items

import com.github.hotm.HotMConstants.message
import com.github.hotm.HotMConstants.str
import com.github.hotm.auranet.DependableAuraNode
import com.github.hotm.auranet.DependantAuraNode
import com.github.hotm.auranet.DependencyAuraNodeUtils
import com.github.hotm.blocks.AuraNodeBlock
import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.util.DimBlockPos
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult

class AuraTunerItem(settings: Settings) : Item(settings), InteractionCanceler {
    companion object {
        private val AURA_TUNER_KEY = str("aura_tuner")
        private const val PARENT_NODE_KEY = "parent_node"
    }

    override fun shouldCancelInteraction(usageContext: ItemUsageContext): Boolean {
        return true
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val pos = context.blockPos
        val world = context.world
        val block = world.getBlockState(pos).block
        val player = context.player

        if (block is AuraNodeBlock && player != null) {
            val node = StorageUtils.getAuraNetAccess(world)[pos]
            val stack = context.stack

            if (node is DependableAuraNode && player.isSneaking) {
                val compound = NbtCompound()
                val parentPos = DimBlockPos(world.registryKey, pos.toImmutable())

                if (!world.isClient) {
                    player.sendMessage(
                        message(
                            "aura_tuner.parent_selected",
                            "(${parentPos.pos.x}, ${parentPos.pos.y}, ${parentPos.pos.z}) in ${parentPos.dim.value}"
                        ),
                        false
                    )
                }

                compound.put(PARENT_NODE_KEY, parentPos.toNbt())
                stack.setSubNbt(AURA_TUNER_KEY, compound)

                return if (world.isClient) ActionResult.SUCCESS else ActionResult.CONSUME
            } else if (node is DependantAuraNode && !player.isSneaking) {
                val auraTunerData = stack.getSubNbt(AURA_TUNER_KEY)
                if (auraTunerData == null) {
                    if (!world.isClient) {
                        player.sendMessage(message("aura_tuner.no_parent"), false)
                    }
                    return ActionResult.FAIL
                } else {
                    if (world.isClient) {
                        return ActionResult.SUCCESS
                    } else {
                        world as ServerWorld

                        val parentPos = DimBlockPos.fromNbt(auraTunerData.getCompound(PARENT_NODE_KEY))
                        val parentNode = parentPos.getAuraNode(world.server)

                        if (parentNode is DependableAuraNode) {
                            val str = when (DependencyAuraNodeUtils.connect(world, parentNode, node)) {
                                DependencyAuraNodeUtils.ConnectionError.NONE -> {
                                    player.sendMessage(message("aura_tuner.success"), false)
                                    return ActionResult.CONSUME
                                }
                                DependencyAuraNodeUtils.ConnectionError.WRONG_DIMENSION -> "wrong_dimension"
                                DependencyAuraNodeUtils.ConnectionError.TOO_FAR -> "too_far"
                                DependencyAuraNodeUtils.ConnectionError.BLOCKED -> "blocked"
                                DependencyAuraNodeUtils.ConnectionError.REJECTED_CHILD -> "rejected_child"
                                DependencyAuraNodeUtils.ConnectionError.REJECTED_PARENT -> "rejected_parent"
                                DependencyAuraNodeUtils.ConnectionError.DEPENDENCY_LOOP -> "dependency_loop"
                            }

                            player.sendMessage(message("aura_tuner.${str}"), false)
                            return ActionResult.FAIL
                        } else {
                            player.sendMessage(message("aura_tuner.parent_not_dependable"), false)
                            return ActionResult.FAIL
                        }
                    }
                }
            } else {
                return ActionResult.PASS
            }
        } else {
            return ActionResult.PASS
        }
    }
}
