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
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap
import net.minecraft.util.math.BlockPos
import java.util.stream.Stream

class CollectorDistributorAuraNode(
    access: AuraNetAccess,
    updateListener: Runnable?,
    pos: BlockPos,
    value: Int,
    parents: Collection<BlockPos>,
    children: Collection<BlockPos>
) : AbstractAuraNode(Type, access, updateListener, pos), DependantAuraNode, RenderedDependableAuraNode {

    companion object {
        private val NET_PARENT = AuraNode.NET_ID.subType(
            CollectorDistributorAuraNode::class.java,
            str("collector_distributor_aura_node")
        )

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
        private val ID_CHILDREN_CHANGE = NET_PARENT.idData("CHILDREN_CHANGE").s2cReadWrite(
            {
                children.clear()
                val childCount = it.readVarUnsignedInt()
                for (i in 0 until childCount) {
                    children.add(it.readBlockPos())
                }
            },
            {
                it.writeVarUnsignedInt(children.size)
                for (child in children) {
                    it.writeBlockPos(child)
                }
            }
        )
    }

    var value = value
        private set
    private val parents = parents.toMutableSet()
    private val children = children.toMutableSet()

    override val maxDistance = 32.0

    /* Crown render variables */
    private var lastRenderWorldTime = world.time
    private var lastRenderTickDelta = 0f
    private val crownRolls = Object2FloatOpenHashMap<BlockPos>()

    private fun updateValue(value: Int, visitedNodes: MutableSet<DimBlockPos>) {
        this.value = value
        markDirty()
        ID_VALUE_CHANGE.sendToClients(world, pos, this)
        for (child in children) {
            AuraNodeUtils.nodeAt<DependantAuraNode>(child, access)?.recalculateDescendants(visitedNodes)
        }
    }

    private fun updateParents() {
        markDirty()
        ID_PARENTS_CHANGE.sendToClients(world, pos, this)
    }

    private fun updateChildren() {
        markDirty()
        ID_CHILDREN_CHANGE.sendToClients(world, pos, this)
    }

    override fun isChildValid(node: DependantAuraNode): Boolean {
        return true
    }

    override fun addChild(node: DependantAuraNode) {
        children.add(node.pos.toImmutable())
        updateChildren()
    }

    override fun removeChild(pos: BlockPos) {
        children.remove(pos.toImmutable())
        updateChildren()
    }

    override fun getSuppliedAura(child: DependantAuraNode): Int {
        return value / children.size
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

            var res = false
            for (child in children) {
                val node = AuraNodeUtils.nodeAt<DependantAuraNode>(child, access)
                if (node != null && node.wouldCauseDependencyLoop(potentialAncestor, visitedNodes)) {
                    res = true
                    break
                }
            }

            visitedNodes.remove(dimPos)

            return res
        }
    }

    override fun addParent(node: DependableAuraNode) {
        parents.add(node.pos.toImmutable())
        updateParents()
    }

    override fun removeParent(pos: BlockPos) {
        parents.remove(pos.toImmutable())
        updateParents()
    }

    override fun recalculateDescendants(visitedNodes: MutableSet<DimBlockPos>) {
        if (visitedNodes.contains(dimPos)) {
            disconnectAll()
            return
        }

        visitedNodes.add(dimPos)

        updateValue(
            parents.stream().flatMap { StreamUtils.ofNullable(AuraNodeUtils.nodeAt<DependableAuraNode>(it, access)) }
                .mapToInt { it.getSuppliedAura(this) }.sum(), visitedNodes
        )

        visitedNodes.remove(dimPos)
    }

    override fun getChildrenForRender(): Stream<BlockPos> {
        return children.stream()
    }

    override fun getSuppliedAuraForRender(pos: BlockPos): Int {
        return value / children.size
    }

    override fun updateRenderValues(worldTime: Long, tickDelta: Float) {
        val dwt = worldTime - lastRenderWorldTime
        val dtd = tickDelta - lastRenderTickDelta
        lastRenderWorldTime = worldTime
        lastRenderTickDelta = tickDelta

        val diff = dwt.toFloat() + dtd

        for (child in children) {
            crownRolls.addTo(child, diff * 3f * (value / children.size).toFloat())
        }
    }

    override fun getCrownRoll(pos: BlockPos): Float {
        return crownRolls.getFloat(pos)
    }

    override fun writeToPacket(buf: NetByteBuf, ctx: IMsgWriteCtx) {
        buf.writeVarUnsignedInt(value)

        buf.writeVarUnsignedInt(parents.size)
        for (parent in parents) {
            buf.writeBlockPos(parent)
        }

        buf.writeVarUnsignedInt(children.size)
        for (child in children) {
            buf.writeBlockPos(child)
        }
    }

    override fun onRemove() {
        disconnectAll()
    }

    private fun disconnectAll() {
        val parentsCopy = parents.toList()
        for (parent in parentsCopy) {
            DependencyAuraNodeUtils.childDisconnect(parent, access, this)
        }

        val childrenCopy = children.toList()
        for (child in childrenCopy) {
            DependencyAuraNodeUtils.parentDisconnect(child, access, this)
        }
    }

    object Type : AuraNodeType<CollectorDistributorAuraNode> {
        override fun createCodec(
            access: AuraNetAccess,
            updateListener: Runnable,
            pos: BlockPos
        ): Codec<CollectorDistributorAuraNode> {
            return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<CollectorDistributorAuraNode> ->
                instance.group(
                    RecordCodecBuilder.point(access),
                    RecordCodecBuilder.point(updateListener),
                    RecordCodecBuilder.point(pos),
                    Codec.INT.fieldOf("value").forGetter(CollectorDistributorAuraNode::value),
                    BlockPos.CODEC.listOf().fieldOf("parents").forGetter { it.parents.toList() },
                    BlockPos.CODEC.listOf().fieldOf("children").forGetter { it.children.toList() }
                ).apply(instance, ::CollectorDistributorAuraNode)
            }
        }

        override fun fromPacket(
            access: AuraNetAccess,
            pos: BlockPos,
            buf: NetByteBuf,
            ctx: IMsgReadCtx
        ): CollectorDistributorAuraNode {
            val value = buf.readVarUnsignedInt()

            val parentCount = buf.readVarUnsignedInt()
            val parents = mutableSetOf<BlockPos>()

            for (i in 0 until parentCount) {
                parents.add(buf.readBlockPos())
            }

            val childCount = buf.readVarUnsignedInt()
            val children = mutableSetOf<BlockPos>()

            for (i in 0 until childCount) {
                children.add(buf.readBlockPos())
            }

            return CollectorDistributorAuraNode(access, null, pos, value, parents, children)
        }
    }
}