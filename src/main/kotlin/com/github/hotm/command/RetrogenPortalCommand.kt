package com.github.hotm.command

import com.github.hotm.HotMConstants
import com.github.hotm.gen.HotMDimensions
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.*
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper

object RetrogenPortalCommand {
    private const val COMMAND_NAME = "retrogen_nectere_portal"
    private val FAILED_EXCEPTION = SimpleCommandExceptionType(HotMConstants.commandText("$COMMAND_NAME.failed"))
    private val WRONG_WORLD_EXCEPTION =
        SimpleCommandExceptionType(HotMConstants.commandText("$COMMAND_NAME.wrong_world"))

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal(COMMAND_NAME).requires { it.hasPermissionLevel(2) }
            .executes(RetrogenPortalCommand::execute)

        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        val source = context.source
        val sourcePos = BlockPos(source.position)
        val sourceWorld = source.world

        if (sourceWorld.registryKey == HotMDimensions.NECTERE_KEY) {
            throw WRONG_WORLD_EXCEPTION.create()
        }

        return when (val res = HotMDimensions.retrogenNonNectereSidePortal(source.world, sourcePos, 100)) {
            HotMDimensions.RetrogenPortalResult.Failure -> throw FAILED_EXCEPTION.create()
            is HotMDimensions.RetrogenPortalResult.Found -> {
                sendCoordinates(source, sourcePos, res.blockPos, "found", false)
                0
            }
            is HotMDimensions.RetrogenPortalResult.Generated -> {
                sendCoordinates(source, sourcePos, res.blockPos, "generated", true)
                1
            }
        }
    }

    private fun sendCoordinates(
        source: ServerCommandSource,
        sourcePos: BlockPos,
        structurePos: BlockPos,
        suffix: String,
        broadcastToOps: Boolean
    ) {
        val i = MathHelper.floor(MathHelper.sqrt(sourcePos.getSquaredDistance(structurePos)))
        val text: Text =
            Texts.bracketed(TranslatableText("chat.coordinates", structurePos.x, structurePos.y, structurePos.z))
                .styled { style: Style ->
                    style.withColor(Formatting.GREEN).withClickEvent(
                        ClickEvent(
                            ClickEvent.Action.SUGGEST_COMMAND,
                            "/tp @s ${structurePos.x} ${structurePos.y} ${structurePos.z}"
                        )
                    ).withHoverEvent(
                        HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            TranslatableText("chat.coordinates.tooltip")
                        )
                    )
                }
        source.sendFeedback(HotMConstants.commandText("$COMMAND_NAME.$suffix", text, i), broadcastToOps)
    }
}