package com.github.hotm.mod.util

import org.quiltmc.qsl.networking.api.PlayerLookup
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.ChunkPos
import alexiil.mc.lib.net.NetByteBuf
import alexiil.mc.lib.net.NetIdDataK
import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil
import com.kneelawk.graphlib.api.graph.LinkEntityContext
import com.kneelawk.graphlib.api.graph.NodeEntityContext

fun <T> NetIdDataK<T>.s2cReadWrite(receiver: T.(NetByteBuf) -> Unit, writer: T.(NetByteBuf) -> Unit): NetIdDataK<T> {
    setReadWrite({ obj, buf, ctx ->
        ctx.assertClientSide()
        obj.receiver(buf)
    }, { obj, buf, ctx ->
        ctx.assertServerSide()
        obj.writer(buf)
    })
    return this
}

fun <T> NetIdDataK<T>.sendToClients(context: LinkEntityContext, obj: T) {
    val world = context.blockWorld as? ServerWorld
        ?: throw IllegalStateException("sendToClients should only be called on the logical server")

    val chunkPos1 = ChunkPos(context.firstBlockPos)
    val chunkPos2 = ChunkPos(context.secondBlockPos)
    val chunkManager = world.chunkManager

    if (chunkManager.isChunkLoaded(chunkPos1.x, chunkPos1.z) && chunkManager.isChunkLoaded(chunkPos2.x, chunkPos2.z)) {
        val playersTrackingFirst = PlayerLookup.tracking(world, chunkPos1).toSet()

        for (player in PlayerLookup.tracking(world, chunkPos2)) {
            if (player in playersTrackingFirst) {
                val connection = CoreMinecraftNetUtil.getConnection(player)
                send(connection, obj)
            }
        }
    }
}

fun <T> NetIdDataK<T>.sendToClients(context: NodeEntityContext, obj: T) {
    val world = context.blockWorld as? ServerWorld
        ?: throw IllegalStateException("sendToClients should only be called on the logical server")

    val chunkPos = ChunkPos(context.blockPos)

    if (world.chunkManager.isChunkLoaded(chunkPos.x, chunkPos.z)) {
        for (connection in CoreMinecraftNetUtil.getPlayersWatching(world, context.blockPos)) {
            send(connection, obj)
        }
    }
}
