package com.github.hotm.net

import alexiil.mc.lib.net.NetByteBuf
import alexiil.mc.lib.net.NetIdDataK
import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World

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

fun <T> NetIdDataK<T>.sendToClients(world: World, pos: BlockPos, obj: T) {
    val chunkPos = ChunkPos(pos)

    if (world.chunkManager.isChunkLoaded(chunkPos.x, chunkPos.z)) {
        for (connection in CoreMinecraftNetUtil.getPlayersWatching(world, pos)) {
            send(connection, obj)
        }
    }
}

fun <T, V> NetIdDataK<T>.s2cCollectionReadWrite(
    collectionSupplier: T.() -> MutableCollection<V>,
    reader: (NetByteBuf) -> V,
    writer: (NetByteBuf, V) -> Unit
): NetIdDataK<T> {
    return s2cReadWrite(
        { buf ->
            val c = collectionSupplier()
            val count = buf.readVarUnsignedInt()
            c.clear()
            for (i in 0 until count) {
                c.add(reader(buf))
            }
        },
        { buf ->
            val c = collectionSupplier()
            buf.writeVarUnsignedInt(c.size)
            for (v in c) {
                writer(buf, v)
            }
        }
    )
}
