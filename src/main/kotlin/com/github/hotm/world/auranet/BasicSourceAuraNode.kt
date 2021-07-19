package com.github.hotm.world.auranet

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.HotMConstants.str
import com.github.hotm.net.s2cReadWrite
import com.github.hotm.net.sendToClients
import com.github.hotm.util.DimBlockPos
import com.github.hotm.util.StreamUtils
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos

class BasicSourceAuraNode(
    access: AuraNetAccess,
    updateListener: Runnable?,
    pos: BlockPos,
    value: Int,
    parents: List<BlockPos>
) : AbstractAuraNode(Type, access, updateListener, pos), SourceAuraNode, DependantAuraNode {

    companion object {
        private val NET_PARENT = AuraNode.NET_ID.subType(BasicSourceAuraNode::class.java, str("basic_source_aura_node"))

        private val ID_VALUE_CHANGE = NET_PARENT.idData("VALUE_CHANGE")
            .s2cReadWrite({ value = it.readVarUnsignedInt() }, { it.writeVarUnsignedInt(value) })
        private val ID_PARENTS_CHANGE = NET_PARENT.idData("PARENTS_CHANGE").s2cReadWrite(
            {
                parents.clear()
                val parentCount = it.readVarUnsignedInt()
                for (i in 0 until parentCount) {
                    parents.add(it.readBlockPos())
                }
            },
            {
                it.writeVarUnsignedInt(parents.size)
                for (parent in parents) {
                    it.writeBlockPos(parent)
                }
            }
        )
    }

    var value = value
        private set

    private val parents = parents.toMutableList()

    override val maxDistance = 32.0

    fun updateValue(value: Int, visitedNodes: MutableSet<DimBlockPos>) {
        this.value = value
        markDirty()
        ID_VALUE_CHANGE.sendToClients(world, pos, this)
        access.recalculateSiphons(ChunkSectionPos.from(pos), visitedNodes)
    }

    fun updateParents() {
        markDirty()
        ID_PARENTS_CHANGE.sendToClients(world, pos, this)
    }

    override fun getSourceAura(): Int {
        return value
    }

    override fun writeToPacket(buf: NetByteBuf, ctx: IMsgWriteCtx) {
        buf.writeVarUnsignedInt(value)
        buf.writeVarUnsignedInt(parents.size)
        for (parent in parents) {
            buf.writeBlockPos(parent)
        }
    }

    override fun isParentValid(node: DependableAuraNode): Boolean {
        return true
    }

    override fun wouldCauseDependencyLoop(
        potentialAncestor: DimBlockPos,
        visitedNodes: MutableSet<DimBlockPos>
    ): Boolean {
        return if (potentialAncestor == dimPos || visitedNodes.contains(dimPos)) {
            true
        } else {
            visitedNodes.add(dimPos)

            val res = access.getAllBy(ChunkSectionPos.from(pos)) { it is SiphonAuraNode }.anyMatch { siphon ->
                siphon as SiphonAuraNode
                siphon.wouldCauseDependencyLoop(potentialAncestor, visitedNodes)
            }

            visitedNodes.remove(dimPos)

            res
        }
    }

    override fun addParent(node: DependableAuraNode) {
        parents.add(node.pos)
        updateParents()
    }

    override fun removeParent(pos: BlockPos) {
        parents.remove(pos)
        updateParents()
    }

    override fun recalculateDescendants(visitedNodes: MutableSet<DimBlockPos>) {
        if (visitedNodes.contains(dimPos)) {
            // Dependency loop detected. Disconnect all parent nodes, as that is the only place the dependency loop
            // could be coming from.
            val parentsCopy = parents.toList()
            for (parent in parentsCopy) {
                DependencyAuraNodeUtils.childDisconnect(parent, access, this)
            }

            return
        }

        visitedNodes.add(dimPos)

        updateValue(
            parents.stream().flatMap { StreamUtils.ofNullable(AuraNodeUtils.nodeAt<DependableAuraNode>(it, access)) }
                .mapToInt { it.getSuppliedAura(this) }.sum(), visitedNodes
        )

        visitedNodes.remove(dimPos)
    }

    object Type : AuraNodeType<BasicSourceAuraNode> {
        override fun createCodec(
            access: AuraNetAccess,
            updateListener: Runnable,
            pos: BlockPos
        ): Codec<BasicSourceAuraNode> {
            return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<BasicSourceAuraNode> ->
                instance.group(
                    RecordCodecBuilder.point(access),
                    RecordCodecBuilder.point(updateListener),
                    RecordCodecBuilder.point(pos),
                    Codec.INT.fieldOf("value").forGetter(BasicSourceAuraNode::value),
                    BlockPos.CODEC.listOf().fieldOf("parents").forGetter(BasicSourceAuraNode::parents)
                ).apply(instance, ::BasicSourceAuraNode)
            }
        }

        override fun fromPacket(
            access: AuraNetAccess,
            pos: BlockPos,
            buf: NetByteBuf,
            ctx: IMsgReadCtx
        ): BasicSourceAuraNode {
            val value = buf.readVarUnsignedInt()
            val parentCount = buf.readVarUnsignedInt()
            val parents = mutableListOf<BlockPos>()

            for (i in 0 until parentCount) {
                parents.add(buf.readBlockPos())
            }

            return BasicSourceAuraNode(access, null, pos, value, parents)
        }
    }
}