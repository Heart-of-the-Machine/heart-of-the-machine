package com.github.hotm.net

import alexiil.mc.lib.net.IMsgReadCtx
import alexiil.mc.lib.net.NetByteBuf
import alexiil.mc.lib.net.NetIdData
import alexiil.mc.lib.net.NetObjectCache
import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil
import alexiil.mc.lib.net.impl.McNetworkStack
import com.github.hotm.HotMConstants.str
import com.github.hotm.misc.HotMRegistries
import com.github.hotm.mixinapi.StorageUtils
import com.github.hotm.world.auranet.client.ClientAuraNetStorage
import com.github.hotm.world.auranet.server.ServerAuraNetStorage
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.minecraft.client.MinecraftClient
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.ChunkSectionPos
import org.apache.logging.log4j.LogManager

object HotMNetwork {
    private val LOGGER = LogManager.getLogger()

    private val ID_AURA_NET_TYPE_ID_CACHE = McNetworkStack.ROOT.child(str("AURA_NET_TYPE_ID_CACHE"))
    val AURA_NET_TYPE_ID_CACHE = NetObjectCache.createMappedIdentifier(ID_AURA_NET_TYPE_ID_CACHE, { type ->
        HotMRegistries.AURA_NODE_TYPE.getId(type) ?: throw IllegalArgumentException(
            "Attempting to encode an AuraNodeType that has not been registered! type: $type"
        )
    }, HotMRegistries.AURA_NODE_TYPE::get)

    private val ID_S2C_AURA_NET_CHUNK_PILLAR =
        McNetworkStack.ROOT.idData(str("AURA_NET_CHUNK_PILLAR")).setReceiver(::receiveAuraNetChunkPillar)
    private val ID_S2C_AURA_NODE_PUT =
        McNetworkStack.ROOT.idData(str("AURA_NODE_PUT")).setReceiver(::receiveAuraNodePut)
    private val ID_S2C_AURA_NODE_REMOVE =
        McNetworkStack.ROOT.idData(str("AURA_NODE_REMOVE")).setReceiver(::receiveAuraNodeRemove)
    private val ID_S2C_BASE_AURA_UPDATE =
        McNetworkStack.ROOT.idData(str("BASE_AURA_UPDATE")).setReceiver(::receiveBaseAuraUpdate)

    private fun getClientStorage(ctx: IMsgReadCtx, errorMsg: String): ClientAuraNetStorage? {
        ctx.assertClientSide()
        val world = MinecraftClient.getInstance().world

        if (world == null) {
            LOGGER.warn(errorMsg)
            return null
        }

        return StorageUtils.getClientAuraNetStorage(world)
    }

    private fun receiveAuraNetChunkPillar(buf: NetByteBuf, ctx: IMsgReadCtx) {
        val storage = getClientStorage(ctx, "Received chunk pillar update packet while not in a world") ?: return
        storage.receiveChunkPillar(buf, ctx)
    }

    @JvmStatic
    fun sendAuraNetChunkPillar(storage: ServerAuraNetStorage, player: ServerPlayerEntity, pos: ChunkPos) {
        val conn = CoreMinecraftNetUtil.getConnection(player)
        ID_S2C_AURA_NET_CHUNK_PILLAR.send(conn) { buf, ctx ->
            storage.sendChunkPillar(pos, buf, ctx)
        }
    }

    private fun receiveAuraNodePut(buf: NetByteBuf, ctx: IMsgReadCtx) {
        val storage = getClientStorage(ctx, "Received aura node remove packet while not in a world") ?: return
        storage.receivePut(buf, ctx)
    }

    fun sendAuraNodePut(world: ServerWorld, pos: BlockPos, writer: NetIdData.IMsgDataWriter) {
        CoreMinecraftNetUtil.getPlayersWatching(world, pos).forEach { conn ->
            ID_S2C_AURA_NODE_PUT.send(conn, writer)
        }
    }

    private fun receiveAuraNodeRemove(buf: NetByteBuf, ctx: IMsgReadCtx) {
        val storage = getClientStorage(ctx, "Received aura node remove packet while not in a world") ?: return
        storage.receiveRemove(buf, ctx)
    }

    fun sendAuraNodeRemove(world: ServerWorld, pos: BlockPos, writer: NetIdData.IMsgDataWriter) {
        CoreMinecraftNetUtil.getPlayersWatching(world, pos).forEach { conn ->
            ID_S2C_AURA_NODE_REMOVE.send(conn, writer)
        }
    }

    private fun receiveBaseAuraUpdate(buf: NetByteBuf, ctx: IMsgReadCtx) {
        val storage = getClientStorage(ctx, "Received base aura update packet while not in a world") ?: return
        storage.receiveBaseAuraUpdate(buf, ctx)
    }

    fun sendBaseAuraUpdate(world: ServerWorld, pos: ChunkSectionPos, writer: NetIdData.IMsgDataWriter) {
        for (player in PlayerLookup.tracking(world, pos.toChunkPos())) {
            val conn = CoreMinecraftNetUtil.getConnection(player)
            ID_S2C_BASE_AURA_UPDATE.send(conn, writer)
        }
    }
}