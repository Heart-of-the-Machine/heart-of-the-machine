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
import java.util.*
import java.util.stream.Stream

class BasicSiphonAuraNode(
    access: AuraNetAccess,
    updateListener: Runnable?,
    pos: BlockPos,
    value: Int,
    childPos: BlockPos?
) : AbstractAuraNode(Type, access, updateListener, pos), SiphonAuraNode, RenderedDependableAuraNode {

    companion object {
        private val NET_PARENT = AuraNode.NET_ID.subType(
            BasicSiphonAuraNode::class.java,
            str("basic_siphon_aura_node")
        )

        private val ID_VALUE_CHANGE = NET_PARENT.idData("VALUE_CHANGE")
            .s2cReadWrite({ value = it.readVarUnsignedInt() }, { it.writeVarUnsignedInt(value) })
        private val ID_CHILD_POS_CHANGE = NET_PARENT.idData("CHILD_POS_CHANGE")
            .s2cReadWrite(
                { childPos = if (it.readBoolean()) it.readBlockPos() else null },
                {
                    val childPos = childPos
                    it.writeBoolean(childPos != null)
                    childPos?.let { pos -> it.writeBlockPos(pos) }
                })
    }

    var value = value
        private set

    var childPos = childPos
        private set

    override val maxDistance = 32.0

    /* Crown render variables */

    private var lastRenderWorldTime = world.time
    private var lastRenderTickDelta = 0f
    private var crownRoll = 0f

    fun updateValue(value: Int, visitedNodes: MutableSet<DimBlockPos>) {
        this.value = value
        markDirty()
        ID_VALUE_CHANGE.sendToClients(world, pos, this)
        AuraNodeUtils.nodeAt<DependantAuraNode>(childPos, access)?.recalculateDescendants(visitedNodes)
    }

    fun updateChildPos(childPos: BlockPos?) {
        this.childPos = childPos
        markDirty()
        ID_CHILD_POS_CHANGE.sendToClients(world, pos, this)
    }

    override fun wouldCauseDependencyLoop(
        potentialAncestor: DimBlockPos,
        visitedNodes: MutableSet<DimBlockPos>
    ): Boolean {
        return if (potentialAncestor == dimPos || visitedNodes.contains(dimPos)) {
            true
        } else {
            visitedNodes.add(dimPos)

            val res = AuraNodeUtils.nodeAt<DependantAuraNode>(childPos, access)
                ?.wouldCauseDependencyLoop(potentialAncestor, visitedNodes) ?: false

            visitedNodes.remove(dimPos)

            res
        }
    }

    override fun recalculateSiphonValue(chunkAura: Int, siphonCount: Int, visitedNodes: MutableSet<DimBlockPos>) {
        if (visitedNodes.contains(dimPos)) {
            // Dependency loop detected, disconnect the child because that's the only place the loop could be coming
            // from.
            DependencyAuraNodeUtils.parentDisconnect(childPos, access, this)
            return
        }

        visitedNodes.add(dimPos)

        updateValue(AuraNodeUtils.calculateSiphonValue(10f, 2f, chunkAura, siphonCount), visitedNodes)

        visitedNodes.remove(dimPos)
    }

    override fun isChildValid(node: DependantAuraNode): Boolean {
        return true
    }

    override fun addChild(node: DependantAuraNode) {
        // remove previous child
        DependencyAuraNodeUtils.parentDisconnect(childPos, access, this)

        updateChildPos(node.pos)
    }

    override fun removeChild(pos: BlockPos) {
        if (pos != childPos) return

        updateChildPos(null)
    }

    override fun getSuppliedAura(child: DependantAuraNode): Int {
        return value
    }

    override fun getChildrenForRender(): Stream<BlockPos> {
        return StreamUtils.ofNullable(childPos)
    }

    override fun getSuppliedAuraForRender(pos: BlockPos): Int {
        return value
    }

    override fun getCrownRoll(worldTime: Long, tickDelta: Float, pos: BlockPos): Float {
        val dwt = worldTime - lastRenderWorldTime
        val dtd = tickDelta - lastRenderTickDelta
        lastRenderWorldTime = worldTime
        lastRenderTickDelta = tickDelta

        val diff = dwt.toFloat() + dtd
        crownRoll += diff * value.toFloat() * 3f

        return crownRoll
    }

    override fun writeToPacket(buf: NetByteBuf, ctx: IMsgWriteCtx) {
        buf.writeVarUnsignedInt(value)

        val childPos = childPos
        if (childPos != null) {
            buf.writeBoolean(true)
            buf.writeBlockPos(childPos)
        } else {
            buf.writeBoolean(false)
        }
    }

    override fun onRemove() {
        // Remove self from the child's parents
        DependencyAuraNodeUtils.parentDisconnect(childPos, access, this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as BasicSiphonAuraNode

        if (value != other.value) return false
        if (childPos != other.childPos) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + value
        result = 31 * result + (childPos?.hashCode() ?: 0)
        return result
    }

    object Type : AuraNodeType<BasicSiphonAuraNode> {
        override fun createCodec(
            access: AuraNetAccess,
            updateListener: Runnable,
            pos: BlockPos
        ): Codec<BasicSiphonAuraNode> {
            return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<BasicSiphonAuraNode> ->
                instance.group(
                    RecordCodecBuilder.point(access),
                    RecordCodecBuilder.point(updateListener),
                    RecordCodecBuilder.point(pos),
                    Codec.INT.fieldOf("value").forGetter(BasicSiphonAuraNode::value),
                    BlockPos.CODEC.optionalFieldOf("child_pos").forGetter { Optional.ofNullable(it.childPos) }
                ).apply(instance) { access, updateListener, pos, value, childPos ->
                    BasicSiphonAuraNode(access, updateListener, pos, value, childPos.orElse(null))
                }
            }
        }

        override fun fromPacket(
            access: AuraNetAccess, pos: BlockPos, buf: NetByteBuf, ctx: IMsgReadCtx
        ): BasicSiphonAuraNode {
            val value = buf.readVarUnsignedInt()
            val childPos = if (buf.readBoolean()) buf.readBlockPos() else null
            return BasicSiphonAuraNode(access, null, pos, value, childPos)
        }
    }
}