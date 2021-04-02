package com.github.hotm.world.auranet.server

import com.github.hotm.EmptyClassLoader
import com.github.hotm.world.auranet.AuraNode
import com.github.hotm.world.auranet.SiphonAuraNode
import com.github.hotm.world.auranet.SourceAuraNode
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

class ServerAuraNetChunkTests : FunSpec({
    isolationMode = IsolationMode.InstancePerTest

    context("Context: Basic ServerAuraNetChunk") {
        val chunk = ServerAuraNetChunk({}, 64, listOf())

        test("Gives base aura") {
            chunk.getBaseAura() shouldBe 64

            chunk.setBaseAura(32)
            chunk.getBaseAura() shouldBe 32
        }
    }

    context("Context: With a basic updateListener") {
        val updateListener = mockk<Runnable>()
        every { updateListener.run() } just Runs
        val node = mockk<AuraNode>()
        every { node.pos } returns BlockPos(0, 0, 0)
        every { node.onRemove() } just Runs

//        val cl = EmptyClassLoader()
//        val runnable = Runnable {
            val world = mockk<ServerWorld>()
            println("WORLD: $world")
//        }
//        val runnableClass = runnable.javaClass
//        println("Runnable class: $runnableClass")
//        val alteredClass = cl.loadClass(runnableClass.name)
//        val runnable2 = EmptyClassLoader.OBJENISIS.newInstance(alteredClass)
//        val runMethod = alteredClass.getMethod("run")
//        runMethod.isAccessible = true
//        runMethod.invoke(runnable2)

        val chunk = ServerAuraNetChunk(updateListener, 64, listOf())

        test("Calls updateListener on baseAura change") {
            chunk.setBaseAura(32)

            verify { updateListener.run() }
        }

        test("Calls updateListener on node add") {
            chunk.put(node)

            verify { updateListener.run() }
        }
    }

    context("Context: With a basic AuraNode") {
        val node = mockk<AuraNode>()
        every { node.pos } returns BlockPos(0, 0, 0)
        every { node.onRemove() } just Runs

        val chunk = ServerAuraNetChunk({}, 64, listOf(node))

        test("Calls onRemove on a node being removed") {
            chunk.remove(BlockPos(0, 0, 0))

            verify { node.onRemove() }
        }
    }

    context("Context: With a basic updateListener and AuraNode") {
        val updateListener = mockk<Runnable>()
        every { updateListener.run() } just Runs
        val node = mockk<AuraNode>()
        every { node.pos } returns BlockPos(0, 0, 0)
        every { node.onRemove() } just Runs

        val chunk = ServerAuraNetChunk(updateListener, 64, listOf(node))

        test("Calls updateListener on node remove") {
            chunk.remove(BlockPos(0, 0, 0))

            verify { updateListener.run() }
        }
    }

    context("Context: With a basic SourceAuraNode") {
        val source = mockk<SourceAuraNode>()
        every { source.pos } returns BlockPos(0, 0, 0)
        every { source.getSourceAura() } returns 1

        val chunk = ServerAuraNetChunk({}, 64, listOf(source))

        test("Gives total aura") {
            chunk.getTotalAura() shouldBe 65

            verify { source.getSourceAura() }
        }
    }

    context("Context: With a basic SiphonAuraNode") {
        val siphon = mockk<SiphonAuraNode>()
        every { siphon.pos } returns BlockPos(0, 0, 0)
        every { siphon.recalculateSiphonValue(any(), any()) } just Runs

        val source = mockk<SourceAuraNode>()
        every { source.pos } returns BlockPos(1, 0, 0)
        every { source.getSourceAura() } returns 1

        val chunk = ServerAuraNetChunk({}, 64, listOf(siphon))

        test("Recalculates siphon values on base aura change") {
            chunk.setBaseAura(32)

            verify { siphon.recalculateSiphonValue(32, 1) }
        }

        test("Recalculates siphon values when a source is added") {
            chunk.put(source)

            verify { siphon.recalculateSiphonValue(65, 1) }
        }
    }

    context("Context: With a basic SourceAuraNode and SiphonAuraNode") {
        val siphon = mockk<SiphonAuraNode>()
        every { siphon.pos } returns BlockPos(0, 0, 0)
        every { siphon.recalculateSiphonValue(any(), any()) } just Runs

        val source = mockk<SourceAuraNode>()
        every { source.pos } returns BlockPos(1, 0, 0)
        every { source.getSourceAura() } returns 1
        every { source.onRemove() } just Runs

        val chunk = ServerAuraNetChunk({}, 64, listOf(source, siphon))

        test("Recalculates siphon values when a source is removed") {
            chunk.remove(BlockPos(1, 0, 0))

            verify { siphon.recalculateSiphonValue(64, 1) }
        }
    }
})
