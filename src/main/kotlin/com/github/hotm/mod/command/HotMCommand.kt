package com.github.hotm.mod.command

import java.time.Duration
import com.github.hotm.mod.Constants.msg
import com.github.hotm.mod.HotMLog
import com.github.hotm.mod.world.HotMDimensions
import com.github.hotm.mod.world.HotMPortalFinders
import com.github.hotm.mod.world.HotMPortalOffsets
import com.github.hotm.mod.world.aura.Aura
import com.github.hotm.mod.world.gen.structure.HotMStructures
import com.google.common.base.Stopwatch
import kotlin.jvm.optionals.getOrNull
import org.quiltmc.qkl.library.brigadier.CommandResult
import org.quiltmc.qkl.library.brigadier.argument.literal
import org.quiltmc.qkl.library.brigadier.executeWithResult
import org.quiltmc.qkl.library.brigadier.register
import org.quiltmc.qkl.library.brigadier.required
import org.quiltmc.qkl.library.brigadier.util.sendFeedback
import org.quiltmc.qkl.library.brigadier.util.server
import org.quiltmc.qkl.library.brigadier.util.world
import org.quiltmc.qsl.command.api.CommandRegistrationCallback
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.registry.HolderSet
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.Texts
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.math.MathHelper

object HotMCommand {
    fun init() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register("hotm") {
                requires { it.hasPermissionLevel(2) }
                setupLocate()
                setupAura()
            }
        }
    }

    private fun LiteralArgumentBuilder<ServerCommandSource>.setupLocate() {
        required(literal("locate_nectere_portal")) {
            executeWithResult {
                val worldKey = world.registryKey
                val sourcePos = BlockPos.fromPosition(source.position)

                if (worldKey == HotMDimensions.NECTERE_KEY) {
                    val structureRegistry = server.registryManager.get(RegistryKeys.STRUCTURE_FEATURE)
                    val necterePortalHolder = structureRegistry.getHolder(HotMStructures.NECTERE_PORTAL).getOrNull()
                        ?: return@executeWithResult CommandResult.failure(Text.literal("Error getting nectere portal holder. This is probably a bug."))
                    val holderSet = HolderSet.createDirect(necterePortalHolder)
                    val stopwatch = Stopwatch.createStarted(Util.TICKER)
                    val pair = world.chunkManager.chunkGenerator.findFirst(world, holderSet, sourcePos, 100, false)
                    stopwatch.stop()
                    if (pair == null) {
                        return@executeWithResult CommandResult.failure(msg("locate_nectere_portal.not_found"))
                    } else {
                        val portalPos = HotMPortalOffsets.structure2PortalPos(pair.first)
                        return@executeWithResult CommandResult.success(
                            sendFeedback(source, sourcePos, portalPos, stopwatch.elapsed())
                        )
                    }
                } else {
                    val stopwatch = Stopwatch.createStarted()
                    val structurePos = HotMPortalFinders.locateNonNectereSidePortalStructure(world, sourcePos, 100)
                    stopwatch.stop()
                    if (structurePos == null) {
                        return@executeWithResult CommandResult.failure(msg("locate_nectere_portal.not_found"))
                    } else {
                        val portalPos = HotMPortalOffsets.structure2PortalPos(structurePos)
                        return@executeWithResult CommandResult.success(
                            sendFeedback(source, sourcePos, portalPos, stopwatch.elapsed())
                        )
                    }
                }
            }
        }
    }

    private fun LiteralArgumentBuilder<ServerCommandSource>.setupAura() {
        required(literal("aura")) {
            required(literal("get")) {
                executeWithResult {
                    val aura = Aura.get(world, ChunkSectionPos.from(source.position))

                    sendFeedback(
                        msg(
                            "current_aura.get",
                            Text.literal(aura.toString()).styled { it.withColor(Formatting.AQUA) })
                    )

                    CommandResult.success(aura.toInt())
                }
            }
        }
    }

    private fun sendFeedback(
        source: ServerCommandSource,
        senderPos: BlockPos,
        resultPos: BlockPos,
        duration: Duration
    ): Int {
        val distance = MathHelper.floor(getDistance(senderPos.x, senderPos.z, resultPos.x, resultPos.z))

        val text: Text = Texts.bracketed(
            Text.translatable(
                "chat.coordinates",
                resultPos.x,
                "~",
                resultPos.z
            )
        )
            .styled { textx: Style ->
                textx.withColor(Formatting.GREEN)
                    .withClickEvent(
                        ClickEvent(
                            ClickEvent.Action.SUGGEST_COMMAND,
                            "/tp @s ${resultPos.x} ~ ${resultPos.z}"
                        )
                    )
                    .withHoverEvent(
                        HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            Text.translatable("chat.coordinates.tooltip")
                        )
                    )
            }

        source.sendFeedback({
            Text.translatable(
                "commands.locate.structure.success",
                "nectere portal" as Any,
                text,
                distance
            )
        }, false)
        HotMLog.LOG.info("Locating element nectere portal took " + duration.toMillis() + " ms")
        return distance
    }

    private fun getDistance(x1: Int, y1: Int, x2: Int, y2: Int): Float {
        val i = x2 - x1
        val j = y2 - y1
        return MathHelper.sqrt((i * i + j * j).toFloat())
    }
}
