package com.github.hotm.world.auranet

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.IMsgWriteCtx
import alexiil.mc.lib.net.NetByteBuf
import com.github.hotm.HotMConstants.str
import com.github.hotm.net.s2cCollectionReadWrite
import com.github.hotm.net.s2cReadWrite
import com.github.hotm.net.sendToClients
import com.github.hotm.util.CodecUtils
import com.github.hotm.util.DimBlockPos
import com.github.hotm.util.StreamUtils
import com.github.hotm.world.HotMPortalFinders
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import java.util.*

class PortalTransmitterAuraNode(
    access: AuraNetAccess,
    updateListener: Runnable?,
    pos: BlockPos,
    private var value: Float,
    parents: Collection<BlockPos>,
    private var valid: Boolean,
) : AbstractAuraNode(Type, access, updateListener, pos), DependantAuraNode, PortalTXAuraNode {

    companion object {
        private val NET_PARENT =
            AuraNode.NET_ID.subType(PortalTransmitterAuraNode::class.java, str("portal_transmitter_aura_node"))

        private val ID_VALUE_CHANGE =
            NET_PARENT.idData("VALUE_CHANGE").s2cReadWrite({ value = it.readFloat() }, { it.writeFloat(value) })
        private val ID_PARENTS_CHANGE = NET_PARENT.idData("PARENTS_CHANGE").s2cCollectionReadWrite(
            PortalTransmitterAuraNode::parents,
            NetByteBuf::readBlockPos,
            NetByteBuf::writeBlockPos
        )
        private val ID_VALIDITY_CHANGE =
            NET_PARENT.idData("VALIDITY_CHANGE").s2cReadWrite({ valid = it.readBoolean() }, { it.writeBoolean(valid) })
    }

    private val parents = parents.toMutableSet()

    /* Server-side only */
    private var cachedReceiver: DimBlockPos? = null

    override val maxDistance = 32.0

    private fun findReceiver(): PortalRXAuraNode? {
        return if (!isClient) {
            val world = world
            world as ServerWorld
            val server = world.server

            val receiverPos = HotMPortalFinders.findNecterePortal(world, pos) {
                (it.getAuraNode(server) as? PortalRXAuraNode)?.isValid() ?: false
            }

            cachedReceiver = receiverPos

            receiverPos?.getAuraNode(server) as? PortalRXAuraNode
        } else {
            null
        }
    }

    private fun getReceiver(): PortalRXAuraNode? {
        if (!isClient) {
            val world = world
            world as ServerWorld
            val server = world.server

            cachedReceiver?.let { cached ->
                val node = cached.getAuraNode(server)
                return if (node is PortalRXAuraNode && node.isValid()) {
                    node
                } else {
                    findReceiver()
                }
            }

            return findReceiver()
        }

        return null
    }

    private fun updateValue(value: Float, visitedNodes: MutableSet<DimBlockPos>) {
        this.value = value
        markDirty()
        ID_VALUE_CHANGE.sendToClients(world, pos, this)
        getReceiver()?.recalculateDescendants(visitedNodes)
    }

    private fun updateParents() {
        markDirty()
        ID_PARENTS_CHANGE.sendToClients(world, pos, this)
        // Any time our updateParents method is called, our recalculateDescendants method will also be called.
    }

    fun recalculateValidity() {
        val world = world
        if (!isClient) {
            val prevValid = valid
            valid = PortalAuraNodeUtils.isPortalStructureValid(pos, world as ServerWorld)

            if (prevValid != valid) {
                markDirty()
                ID_VALIDITY_CHANGE.sendToClients(world, pos, this)
                getReceiver()?.recalculateDescendants(hashSetOf(dimPos))
            }
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

            val res = getReceiver()?.wouldCauseDependencyLoop(potentialAncestor, visitedNodes) ?: false

            visitedNodes.remove(dimPos)

            res
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
            DependencyAuraNodeUtils.childDisconnectAll(parents, access, this)
            return
        }

        visitedNodes.add(dimPos)

        updateValue(
            parents.stream().flatMap { StreamUtils.ofNullable(AuraNodeUtils.nodeAt<DependableAuraNode>(it, access)) }
                .mapToDouble { it.getSuppliedAura(this).toDouble() }.sum().toFloat(), visitedNodes
        )

        visitedNodes.remove(dimPos)
    }

    override fun isValid(): Boolean = valid

    override fun getSuppliedAura(): Float = value

    override fun getValue(): Float = value

    override fun writeToPacket(buf: NetByteBuf, ctx: IMsgWriteCtx) {
        buf.writeFloat(value)

        buf.writeVarUnsignedInt(parents.size)
        for (parent in parents) {
            buf.writeBlockPos(parent)
        }

        buf.writeBoolean(valid)
    }

    override fun onRemove() {
        DependencyAuraNodeUtils.childDisconnectAll(parents, access, this)
        getReceiver()?.recalculateDescendants(hashSetOf())
    }

    object Type : AuraNodeType<PortalTransmitterAuraNode> {
        override fun createCodec(
            access: AuraNetAccess,
            updateListener: Runnable,
            pos: BlockPos
        ): Codec<PortalTransmitterAuraNode> {
            return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<PortalTransmitterAuraNode> ->
                instance.group(
                    RecordCodecBuilder.point(access),
                    RecordCodecBuilder.point(updateListener),
                    RecordCodecBuilder.point(pos),
                    CodecUtils.PREFER_FLOAT_OR_INT.fieldOf("value").forGetter(PortalTransmitterAuraNode::value),
                    BlockPos.CODEC.listOf().fieldOf("parents").forGetter { it.parents.toList() },
                    Codec.BOOL.fieldOf("valid").forGetter(PortalTransmitterAuraNode::valid),
                    DimBlockPos.CODEC.optionalFieldOf("cachedReceiver")
                        .forGetter { Optional.ofNullable(it.cachedReceiver) }
                ).apply(instance) { access, updateListener, pos, value, parents, valid, cachedReceiver ->
                    PortalTransmitterAuraNode(
                        access,
                        updateListener,
                        pos,
                        value,
                        parents,
                        valid
                    ).also { it.cachedReceiver = cachedReceiver.orElse(null) }
                }
            }
        }

        override fun fromPacket(
            access: AuraNetAccess,
            pos: BlockPos,
            buf: NetByteBuf,
            ctx: IMsgReadCtx
        ): PortalTransmitterAuraNode {
            val value = buf.readFloat()

            val parentCount = buf.readVarUnsignedInt()
            val parents = mutableSetOf<BlockPos>()
            for (i in 0 until parentCount) {
                parents.add(buf.readBlockPos())
            }

            val valid = buf.readBoolean()

            return PortalTransmitterAuraNode(access, null, pos, value, parents, valid)
        }
    }
}