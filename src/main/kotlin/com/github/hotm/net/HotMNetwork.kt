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
import com.github.hotm.world.meta.client.ClientMetaStorage
import com.github.hotm.world.meta.server.ServerMetaStorage
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

    private val ID_META_BLOCK_TYPE_ID_CACHE = McNetworkStack.ROOT.child(str("META_BLOCK_TYPE_ID_CACHE"))
    val META_BLOCK_TYPE_ID_CACHE = NetObjectCache.createMappedIdentifier(ID_META_BLOCK_TYPE_ID_CACHE, { type ->
        HotMRegistries.META_BLOCK_TYPE.getId(type) ?: throw IllegalArgumentException(
            "Attempting to encode an MetaBlockType that has not been registered! type: $type"
        )
    }, HotMRegistries.META_BLOCK_TYPE::get)

    private val ID_S2C_META_CHUNK_PILLAR =
        McNetworkStack.ROOT.idData(str("META_CHUNK_PILLAR")).setReceiver(::receiveMetaChunkPillar)
    private val ID_S2C_META_BLOCK_PUT =
        McNetworkStack.ROOT.idData(str("META_BLOCK_PUT")).setReceiver(::receiveMetaBlockPut)
    private val ID_S2C_META_BLOCK_REMOVE =
        McNetworkStack.ROOT.idData(str("META_BLOCK_REMOVE")).setReceiver(::receiveMetaBlockRemove)
    private val ID_S2C_BASE_AURA_UPDATE =
        McNetworkStack.ROOT.idData(str("BASE_AURA_UPDATE")).setReceiver(::receiveBaseAuraUpdate)

    private fun getClientStorage(ctx: IMsgReadCtx, errorMsg: String): ClientMetaStorage? {
        ctx.assertClientSide()
        val world = MinecraftClient.getInstance().world

        if (world == null) {
            LOGGER.warn(errorMsg)
            return null
        }

        return StorageUtils.getClientMetaStorage(world)
    }

    private fun receiveMetaChunkPillar(buf: NetByteBuf, ctx: IMsgReadCtx) {
        val storage = getClientStorage(ctx, "Received chunk pillar update packet while not in a world") ?: return
        storage.receiveChunkPillar(buf, ctx)
    }

    @JvmStatic
    fun sendMetaChunkPillar(storage: ServerMetaStorage, player: ServerPlayerEntity, pos: ChunkPos) {
        val conn = CoreMinecraftNetUtil.getConnection(player)
        ID_S2C_META_CHUNK_PILLAR.send(conn) { buf, ctx ->
            storage.sendChunkPillar(pos, buf, ctx)
        }
    }

    private fun receiveMetaBlockPut(buf: NetByteBuf, ctx: IMsgReadCtx) {
        val storage = getClientStorage(ctx, "Received meta block remove packet while not in a world") ?: return
        storage.receivePut(buf, ctx)
    }

    fun sendMetaBlockPut(world: ServerWorld, pos: BlockPos, writer: NetIdData.IMsgDataWriter) {
        CoreMinecraftNetUtil.getPlayersWatching(world, pos).forEach { conn ->
            ID_S2C_META_BLOCK_PUT.send(conn, writer)
        }
    }

    private fun receiveMetaBlockRemove(buf: NetByteBuf, ctx: IMsgReadCtx) {
        val storage = getClientStorage(ctx, "Received meta block remove packet while not in a world") ?: return
        storage.receiveRemove(buf, ctx)
    }

    fun sendMetaBlockRemove(world: ServerWorld, pos: BlockPos, writer: NetIdData.IMsgDataWriter) {
        CoreMinecraftNetUtil.getPlayersWatching(world, pos).forEach { conn ->
            ID_S2C_META_BLOCK_REMOVE.send(conn, writer)
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