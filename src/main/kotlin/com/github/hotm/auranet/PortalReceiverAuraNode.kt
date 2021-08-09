package com.github.hotm.auranet

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.HotMConstants.str
import com.github.hotm.net.s2cReadWrite
import com.github.hotm.net.sendToClients
import com.github.hotm.util.CodecUtils
import com.github.hotm.util.DimBlockPos
import com.github.hotm.world.HotMPortalFinders
import com.github.hotm.world.auranet.AuraNetAccess
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import java.util.*
import java.util.stream.Stream

class PortalReceiverAuraNode(
    access: AuraNetAccess,
    updateListener: Runnable?,
    pos: BlockPos,
    private var value: Float,
    private var childPos: BlockPos?,
    private var valid: Boolean
) : AbstractDependableAuraNode(Type, access, updateListener, pos), RenderedDependableAuraNode, PortalRXAuraNode,
    ValuedAuraNode {

    companion object {
        private val NET_PARENT =
            AuraNode.NET_ID.subType(PortalReceiverAuraNode::class.java, str("portal_receiver_aura_node"))

        private val ID_VALUE_CHANGE =
            NET_PARENT.idData("VALUE_CHANGE").s2cReadWrite({ value = it.readFloat() }, { it.writeFloat(value) })
        private val ID_CHILD_POS_CHNGE = NET_PARENT.idData("CHILD_POS_CHANGE").s2cReadWrite(
            { childPos = if (it.readBoolean()) it.readBlockPos() else null },
            {
                val childPos = childPos
                it.writeBoolean(childPos != null)
                childPos?.let { pos -> it.writeBlockPos(pos) }
            }
        )
        private val ID_VALIDITY_CHANGE =
            NET_PARENT.idData("VALIDITY_CHANGE").s2cReadWrite({ valid = it.readBoolean() }, { it.writeBoolean(valid) })
    }

    /* Server-side only */
    private var cachedTransmitter: DimBlockPos? = null

    override val maxDistance: Double = 32.0
    override val blockable: Boolean = true

    private fun findTransmitter(): PortalTXAuraNode? {
        return if (!isClient) {
            val world = world
            world as ServerWorld
            val server = world.server

            val transmitterPos = HotMPortalFinders.findNecterePortal(world, pos) {
                (it.getAuraNode(server) as? PortalTXAuraNode)?.isValid() ?: false
            }

            cachedTransmitter = transmitterPos

            transmitterPos?.getAuraNode(server) as? PortalTXAuraNode
        } else {
            null
        }
    }

    private fun getTransmitter(): PortalTXAuraNode? {
        if (!isClient) {
            val world = world
            world as ServerWorld
            val server = world.server

            cachedTransmitter?.let { cached ->
                val node = cached.getAuraNode(server)
                return if (node is PortalTXAuraNode && node.isValid()) {
                    node
                } else {
                    findTransmitter()
                }
            }

            return findTransmitter()
        }

        return null
    }

    private fun updateValue(value: Float, visitedNodes: MutableSet<DimBlockPos>) {
        this.value = value
        markDirty()
        ID_VALUE_CHANGE.sendToClients(world, pos, this)
        AuraNodeUtils.nodeAt<DependantAuraNode>(childPos, access)?.recalculateDescendants(visitedNodes)
    }

    private fun updateChildPos(childPos: BlockPos?) {
        this.childPos = childPos
        markDirty()
        ID_CHILD_POS_CHNGE.sendToClients(world, pos, this)
        // No need to update our new child here as the thing that set our child pos will update our child as well.
    }

    fun recalculateValidity() {
        val world = world
        if (!isClient) {
            val prevValid = valid
            valid = PortalAuraNodeUtils.isPortalStructureValid(pos, world as ServerWorld)

            if (prevValid != valid) {
                markDirty()
                ID_VALIDITY_CHANGE.sendToClients(world, pos, this)
                recalculateDescendants(hashSetOf())
            }
        }
    }

    override fun isChildValid(node: DependantAuraNode): Boolean {
        return true
    }

    override fun addChild(node: DependantAuraNode) {
        // remove the previous child
        DependencyAuraNodeUtils.parentDisconnect(childPos, access, this)

        updateChildPos(node.pos.toImmutable())
    }

    override fun removeChild(pos: BlockPos) {
        if (pos != childPos) return

        updateChildPos(null)
    }

    override fun getSuppliedAura(child: DependantAuraNode): Float = value

    override fun getChildren(): Collection<BlockPos> = setOfNotNull(childPos)

    override fun getChildrenForRender(): Stream<BlockPos> = Stream.ofNullable(childPos)

    override fun getSuppliedAuraForRender(pos: BlockPos): Float = value

    override fun getCrownRollSpeed(pos: BlockPos): Float = value * 3f

    override fun isValid(): Boolean = valid

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

    override fun recalculateDescendants(visitedNodes: MutableSet<DimBlockPos>) {
        if (visitedNodes.contains(dimPos)) {
            DependencyAuraNodeUtils.parentDisconnect(childPos, access, this)
            return
        }

        visitedNodes.add(dimPos)

        updateValue(getTransmitter()?.getSuppliedAura() ?: 0f, visitedNodes)

        visitedNodes.remove(dimPos)
    }

    override fun getValue(): Float = value

    override fun writeToPacket(buf: NetByteBuf, ctx: IMsgWriteCtx) {
        buf.writeFloat(value)

        val childPos = childPos
        buf.writeBoolean(childPos != null)
        childPos?.let { buf.writeBlockPos(it) }

        buf.writeBoolean(valid)
    }

    object Type : AuraNodeType<PortalReceiverAuraNode> {
        override fun createCodec(
            access: AuraNetAccess,
            updateListener: Runnable,
            pos: BlockPos
        ): Codec<PortalReceiverAuraNode> {
            return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<PortalReceiverAuraNode> ->
                instance.group(
                    RecordCodecBuilder.point(access),
                    RecordCodecBuilder.point(updateListener),
                    RecordCodecBuilder.point(pos),
                    CodecUtils.PREFER_FLOAT_OR_INT.fieldOf("value").forGetter(PortalReceiverAuraNode::value),
                    BlockPos.CODEC.optionalFieldOf("childPos").forGetter { Optional.ofNullable(it.childPos) },
                    Codec.BOOL.fieldOf("valid").forGetter(PortalReceiverAuraNode::valid),
                    DimBlockPos.CODEC.optionalFieldOf("cachedTransmitter")
                        .forGetter { Optional.ofNullable(it.cachedTransmitter) }
                ).apply(instance) { access, updateListener, pos, value, childPos, valid, cachedTransmitter ->
                    PortalReceiverAuraNode(
                        access,
                        updateListener,
                        pos,
                        value,
                        childPos.orElse(null),
                        valid
                    ).also { it.cachedTransmitter = cachedTransmitter.orElse(null) }
                }
            }
        }

        override fun fromPacket(
            access: AuraNetAccess,
            pos: BlockPos,
            buf: NetByteBuf,
            ctx: IMsgReadCtx
        ): PortalReceiverAuraNode {
            val value = buf.readFloat()
            val childPos = if (buf.readBoolean()) buf.readBlockPos() else null
            val valid = buf.readBoolean()

            return PortalReceiverAuraNode(access, null, pos, value, childPos, valid)
        }
    }
}