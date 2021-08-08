package com.github.hotm.world.meta.server

import com.github.hotm.blocks.BlockWithMeta
import com.github.hotm.runInMockCL
import com.github.hotm.meta.MetaBlock
import com.github.hotm.meta.MetaBlockType
import com.github.hotm.meta.auranet.SiphonAuraNode
import com.github.hotm.meta.auranet.SourceAuraNode
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

class ServerMetaChunkTests : FunSpec({
    runInMockCL {
        isolationMode = IsolationMode.InstancePerTest

        context("Context: Basic ServerMetaChunk") {
            val chunk = ServerMetaChunk({}, 64f, listOf())

            test("Gives base aura") {
                chunk.getBaseAura() shouldBe 64

                chunk.setBaseAura(32f)
                chunk.getBaseAura() shouldBe 32
            }
        }

        context("Context: With a basic updateListener") {
            val updateListener = mockk<Runnable>()
            every { updateListener.run() } just Runs
            val nodeType = mockk<MetaBlockType<*>>()
            val node = mockk<MetaBlock>()
            every { node.pos } returns BlockPos(0, 0, 0)
            every { node.onRemove() } just Runs

            val world = mockk<ServerWorld>()
            val blockState = mockk<BlockState>()
            val block = mockk<BlockWithMeta>()
            every { block.createMetaBlock(any(), any(), any(), any()) } returns node
            every { block.metaBlockType } returns nodeType

            val storage = mockk<ServerMetaStorage>()

            val chunk = ServerMetaChunk(updateListener, 64f, listOf())

            test("Calls updateListener on baseAura change") {
                chunk.setBaseAura(32f)

                verify { updateListener.run() }
            }

            test("Calls updateListener on node add") {
                chunk.put(node)

                verify { updateListener.run() }
            }

            test("Calls updateListener on node add during chunk scan") {
                chunk.updateMetaBlocks(world, storage) { callback ->
                    callback(blockState, block, BlockPos(0, 0, 0))
                }

                verify { updateListener.run() }
            }
        }

        context("Context: With a basic AuraNode") {
            val nodeType = mockk<MetaBlockType<*>>()
            val node = mockk<MetaBlock>()
            every { node.pos } returns BlockPos(0, 0, 0)
            every { node.onRemove() } just Runs
            every { node.type } returns nodeType

            val world = mockk<ServerWorld>()
            val blockState = mockk<BlockState>()
            val block = mockk<BlockWithMeta>()
            every { block.metaBlockType } returns nodeType
            every { block.createMetaBlock(any(), any(), any(), any()) } returns node

            val storage = mockk<ServerMetaStorage>()

            val chunk = ServerMetaChunk({}, 64f, listOf(node))

            test("Calls onRemove on a node being removed") {
                chunk.remove(BlockPos(0, 0, 0))

                verify { node.onRemove() }

                assert(chunk[BlockPos(0, 0, 0)] == null) { "Chunk should return null when asked for a removed node" }
            }

            test("Calls onRemove on a node being removed during chunk scan") {
                chunk.updateMetaBlocks(world, storage) {
                }

                verify { node.onRemove() }

                assert(chunk[BlockPos(0, 0, 0)] == null) { "Chunk should return null when asked for a removed node" }
            }

            test("Does not remove a node if it's block is present during chunk scan") {
                chunk.updateMetaBlocks(world, storage) { callback ->
                    callback(blockState, block, BlockPos(0, 0, 0))
                }

                verify(exactly = 0) { node.onRemove() }

                assert(chunk[BlockPos(0, 0, 0)] == node) { "Chunk should return node when asked for kept node" }
            }
        }

        context("Context: With a basic updateListener and AuraNode") {
            val updateListener = mockk<Runnable>()
            every { updateListener.run() } just Runs
            val nodeType = mockk<MetaBlockType<*>>()
            val node = mockk<MetaBlock>()
            every { node.pos } returns BlockPos(0, 0, 0)
            every { node.onRemove() } just Runs
            every { node.type } returns nodeType

            val world = mockk<ServerWorld>()
            val blockState = mockk<BlockState>()
            val block = mockk<BlockWithMeta>()
            every { block.metaBlockType } returns nodeType
            every { block.createMetaBlock(any(), any(), any(), any()) } returns node

            val storage = mockk<ServerMetaStorage>()

            val chunk = ServerMetaChunk(updateListener, 64f, listOf(node))

            test("Calls updateListener on node remove") {
                chunk.remove(BlockPos(0, 0, 0))

                verify { updateListener.run() }
            }

            test("Calls updateListener on node remove during chunk scan") {
                chunk.updateMetaBlocks(world, storage) {
                }

                verify { updateListener.run() }
            }

            test("Does not call updateListener when a node is not removed during chunk scan") {
                chunk.updateMetaBlocks(world, storage) { callback ->
                    callback(blockState, block, BlockPos(0, 0, 0))
                }

                verify(exactly = 0) { updateListener.run() }
            }
        }

        context("Context: With a basic SourceAuraNode") {
            val source = mockk<SourceAuraNode>()
            every { source.pos } returns BlockPos(0, 0, 0)
            every { source.getSourceAura() } returns 1f

            val chunk = ServerMetaChunk({}, 64f, listOf(source))

            test("Gives total aura") {
                chunk.getTotalAura() shouldBe 65f

                verify { source.getSourceAura() }
            }
        }

        context("Context: With a basic SiphonAuraNode") {
            val siphonType = mockk<MetaBlockType<*>>()
            val siphon = mockk<SiphonAuraNode>()
            every { siphon.pos } returns BlockPos(0, 0, 0)
            every { siphon.recalculateSiphonValue(any(), any(), any()) } just Runs
            every { siphon.type } returns siphonType

            val sourceType = mockk<MetaBlockType<*>>()
            val source = mockk<SourceAuraNode>()
            every { source.pos } returns BlockPos(1, 0, 0)
            every { source.getSourceAura() } returns 1f
            every { source.type } returns sourceType

            val world = mockk<ServerWorld>()
            val siphonBlockState = mockk<BlockState>()
            val siphonBlock = mockk<BlockWithMeta>()
            every { siphonBlock.metaBlockType } returns siphonType
            every { siphonBlock.createMetaBlock(any(), any(), any(), any()) } returns siphon
            val sourceBlockState = mockk<BlockState>()
            val sourceBlock = mockk<BlockWithMeta>()
            every { sourceBlock.metaBlockType } returns sourceType
            every { sourceBlock.createMetaBlock(any(), any(), any(), any()) } returns source

            val storage = mockk<ServerMetaStorage>()

            val chunk = ServerMetaChunk({}, 64f, listOf(siphon))

            test("Recalculates siphon values on base aura change") {
                chunk.setBaseAura(32f)

                verify { siphon.recalculateSiphonValue(32f, 1, any()) }
            }

            test("Recalculates siphon values when a source is added") {
                chunk.put(source)

                verify { siphon.recalculateSiphonValue(65f, 1, any()) }
            }

            test("Recalculates siphon values when a source is added during chunk scan") {
                chunk.updateMetaBlocks(world, storage) { callback ->
                    callback(siphonBlockState, siphonBlock, BlockPos(0, 0, 0))
                    callback(sourceBlockState, sourceBlock, BlockPos(1, 0, 0))
                }

                verify { siphon.recalculateSiphonValue(65f, 1, any()) }
            }
        }

        context("Context: With a basic SourceAuraNode and SiphonAuraNode") {
            val siphonType = mockk<MetaBlockType<*>>()
            val siphon = mockk<SiphonAuraNode>()
            every { siphon.pos } returns BlockPos(0, 0, 0)
            every { siphon.recalculateSiphonValue(any(), any(), any()) } just Runs
            every { siphon.type } returns siphonType

            val sourceType = mockk<MetaBlockType<*>>()
            val source = mockk<SourceAuraNode>()
            every { source.pos } returns BlockPos(1, 0, 0)
            every { source.getSourceAura() } returns 1f
            every { source.onRemove() } just Runs
            every { source.type } returns sourceType

            val world = mockk<ServerWorld>()
            val siphonBlockState = mockk<BlockState>()
            val siphonBlock = mockk<BlockWithMeta>()
            every { siphonBlock.metaBlockType } returns siphonType
            every { siphonBlock.createMetaBlock(any(), any(), any(), any()) } returns siphon
            val sourceBlockState = mockk<BlockState>()
            val sourceBlock = mockk<BlockWithMeta>()
            every { sourceBlock.metaBlockType } returns sourceType
            every { sourceBlock.createMetaBlock(any(), any(), any(), any()) } returns source

            val storage = mockk<ServerMetaStorage>()

            val chunk = ServerMetaChunk({}, 64f, listOf(source, siphon))

            test("Recalculates siphon values when a source is removed") {
                chunk.remove(BlockPos(1, 0, 0))

                verify { siphon.recalculateSiphonValue(64f, 1, any()) }
            }

            test("Recalculates siphon values when a source is removed during chunk scan") {
                chunk.updateMetaBlocks(world, storage) { callback ->
                    callback(siphonBlockState, siphonBlock, BlockPos(0, 0, 0))
                }

                verify { siphon.recalculateSiphonValue(64f, 1, any()) }
            }

            test("Does not recalculate siphon values when no sources are removed during chunk scan") {
                chunk.updateMetaBlocks(world, storage) { callback ->
                    callback(siphonBlockState, siphonBlock, BlockPos(0, 0, 0))
                    callback(sourceBlockState, sourceBlock, BlockPos(1, 0, 0))
                }

                verify(exactly = 0) { siphon.recalculateSiphonValue(any(), any(), any()) }
            }
        }
    }
})
