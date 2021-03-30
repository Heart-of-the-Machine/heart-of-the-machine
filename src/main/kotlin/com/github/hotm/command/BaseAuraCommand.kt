package com.github.hotm.command

import com.github.hotm.HotMConstants
import com.github.hotm.mixinapi.StorageUtils
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.math.MathHelper

object BaseAuraCommand {
    private const val COMMAND_NAME = "base_aura"
    private const val GET_SUBCOMMAND = "get"
    private const val SET_SUBCOMMAND = "set"

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val baseCommand = CommandManager.literal(COMMAND_NAME)
        dispatcher.register(baseCommand)

        val getCommand = baseCommand.then(CommandManager.literal(GET_SUBCOMMAND).requires { it.hasPermissionLevel(2) }
            .executes(::executeGet))
        val setCommand = baseCommand.then(CommandManager.literal(SET_SUBCOMMAND).requires { it.hasPermissionLevel(2) }
            .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 256)).executes(::executeSet)))

        dispatcher.register(getCommand)
        dispatcher.register(setCommand)
    }

    private fun executeGet(context: CommandContext<ServerCommandSource>): Int {
        val sourceWorld = context.source.world
        val storage = StorageUtils.getServerAuraNetStorage(sourceWorld)

        val value = storage.getBaseAura(ChunkSectionPos.from(BlockPos(context.source.position)))
        context.source.sendFeedback(HotMConstants.commandText(COMMAND_NAME, value), false)

        return MathHelper.clamp(value, 0, 15)
    }

    private fun executeSet(context: CommandContext<ServerCommandSource>): Int {
        val sourceWorld = context.source.world
        val storage = StorageUtils.getServerAuraNetStorage(sourceWorld)
        val value = IntegerArgumentType.getInteger(context, "value")

        storage.setBaseAura(ChunkSectionPos.from(BlockPos(context.source.position)), value)
        context.source.sendFeedback(HotMConstants.commandText(COMMAND_NAME, value), false)

        return MathHelper.clamp(value, 0, 15)
    }
}