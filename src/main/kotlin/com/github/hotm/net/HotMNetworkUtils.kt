package com.github.hotm.net

import alexiil.mc.lib.net.NetByteBuf
import alexiil.mc.lib.net.NetIdDataK
import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil
import net.minecraft.util.math.BlockPos
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
    for (connection in CoreMinecraftNetUtil.getPlayersWatching(world, pos)) {
        send(connection, obj)
    }
}
