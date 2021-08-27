package com.github.hotm.client.blockmodel

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3f
import net.minecraft.util.math.Vector4f
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

object QuadEmitterUtils {
    const val ORIGIN_X = 0.5f
    const val ORIGIN_Y = 0.5f
    const val ORIGIN_Z = 0.5f

    private data class Vertex(var vec: Vector4f, val uv: Vec2f) {
        fun angleFromTop(nominalFace: Direction, center: Vec3f): Float {
            val x = vec.x - center.x
            val y = vec.y - center.y
            val z = vec.z - center.z
            return when (nominalFace) {
                Direction.DOWN -> atan2(x, -z)
                Direction.UP -> atan2(x, z)
                Direction.NORTH -> atan2(-x, -y)
                Direction.SOUTH -> atan2(x, -y)
                Direction.WEST -> atan2(z, -y)
                Direction.EAST -> atan2(-z, -y)
            }
        }
    }

    fun uvSquare(
        emitter: QuadEmitter,
        rotationContainer: ModelBakeSettings,
        nominalFace: Direction,
        left: Float,
        bottom: Float,
        right: Float,
        top: Float,
        depth: Float
    ) {
        val transformedFace = Direction.transform(rotationContainer.rotation.matrix, nominalFace)
        val depth = if (abs(depth) < QuadEmitter.CULL_FACE_EPSILON) {
            emitter.cullFace(transformedFace)
            0f
        } else {
            emitter.cullFace(null)
            depth
        }

        emitter.nominalFace(transformedFace)

        val verts = arrayOf(
            Vertex(Vector4f(), Vec2f(left, 1.0f - top)),
            Vertex(Vector4f(), Vec2f(left, 1.0f - bottom)),
            Vertex(Vector4f(), Vec2f(right, 1.0f - bottom)),
            Vertex(Vector4f(), Vec2f(right, 1.0f - top))
        )

        when (nominalFace) {
            Direction.DOWN -> {
                verts[0].vec = Vector4f(left - ORIGIN_X, depth - ORIGIN_Y, top - ORIGIN_Z, 1f)
                verts[1].vec = Vector4f(left - ORIGIN_X, depth - ORIGIN_Y, bottom - ORIGIN_Z, 1f)
                verts[2].vec = Vector4f(right - ORIGIN_X, depth - ORIGIN_Y, bottom - ORIGIN_Z, 1f)
                verts[3].vec = Vector4f(right - ORIGIN_X, depth - ORIGIN_Y, top - ORIGIN_Z, 1f)
            }
            Direction.UP -> {
                verts[0].vec = Vector4f(left - ORIGIN_X, 1f - depth - ORIGIN_Y, 1f - top - ORIGIN_Z, 1f)
                verts[1].vec = Vector4f(left - ORIGIN_X, 1f - depth - ORIGIN_Y, 1f - bottom - ORIGIN_Z, 1f)
                verts[2].vec = Vector4f(right - ORIGIN_X, 1f - depth - ORIGIN_Y, 1f - bottom - ORIGIN_Z, 1f)
                verts[3].vec = Vector4f(right - ORIGIN_X, 1f - depth - ORIGIN_Y, 1f - top - ORIGIN_Z, 1f)
            }
            Direction.NORTH -> {
                verts[0].vec = Vector4f(1.0f - left - ORIGIN_X, top - ORIGIN_Y, depth - ORIGIN_Z, 1f)
                verts[1].vec = Vector4f(1.0f - left - ORIGIN_X, bottom - ORIGIN_Y, depth - ORIGIN_Z, 1f)
                verts[2].vec = Vector4f(1.0f - right - ORIGIN_X, bottom - ORIGIN_Y, depth - ORIGIN_Z, 1f)
                verts[3].vec = Vector4f(1.0f - right - ORIGIN_X, top - ORIGIN_Y, depth - ORIGIN_Z, 1f)
            }
            Direction.SOUTH -> {
                verts[0].vec = Vector4f(left - ORIGIN_X, top - ORIGIN_Y, 1f - depth - ORIGIN_Z, 1f)
                verts[1].vec = Vector4f(left - ORIGIN_X, bottom - ORIGIN_Y, 1f - depth - ORIGIN_Z, 1f)
                verts[2].vec = Vector4f(right - ORIGIN_X, bottom - ORIGIN_Y, 1f - depth - ORIGIN_Z, 1f)
                verts[3].vec = Vector4f(right - ORIGIN_X, top - ORIGIN_Y, 1f - depth - ORIGIN_Z, 1f)
            }
            Direction.WEST -> {
                verts[0].vec = Vector4f(depth - ORIGIN_X, top - ORIGIN_Y, left - ORIGIN_Z, 1f)
                verts[1].vec = Vector4f(depth - ORIGIN_X, bottom - ORIGIN_Y, left - ORIGIN_Z, 1f)
                verts[2].vec = Vector4f(depth - ORIGIN_X, bottom - ORIGIN_Y, right - ORIGIN_Z, 1f)
                verts[3].vec = Vector4f(depth - ORIGIN_X, top - ORIGIN_Y, right - ORIGIN_Z, 1f)
            }
            Direction.EAST -> {
                verts[0].vec = Vector4f(1f - depth - ORIGIN_X, top - ORIGIN_Y, 1f - left - ORIGIN_Z, 1f)
                verts[1].vec = Vector4f(1f - depth - ORIGIN_X, bottom - ORIGIN_Y, 1f - left - ORIGIN_Z, 1f)
                verts[2].vec = Vector4f(1f - depth - ORIGIN_X, bottom - ORIGIN_Y, 1f - right - ORIGIN_Z, 1f)
                verts[3].vec = Vector4f(1f - depth - ORIGIN_X, top - ORIGIN_Y, 1f - right - ORIGIN_Z, 1f)
            }
        }

        verts[0].vec.transform(rotationContainer.rotation.matrix)
        verts[1].vec.transform(rotationContainer.rotation.matrix)
        verts[2].vec.transform(rotationContainer.rotation.matrix)
        verts[3].vec.transform(rotationContainer.rotation.matrix)

        val center = Vec3f()
        for (vert in verts) {
            center.add(vert.vec.x, vert.vec.y, vert.vec.z)
        }
        center.scale(0.25f)

        var initIndex = 0
        for (i in 0 until 4) {
            val angle = verts[i].angleFromTop(transformedFace, center)
            if (angle < -PI.toFloat() / 2f) {
                initIndex = i
                break
            }
        }

        val vert0 = verts[initIndex]
        val vert1 = verts[(initIndex + 1) % 4]
        val vert2 = verts[(initIndex + 2) % 4]
        val vert3 = verts[(initIndex + 3) % 4]

        emitter.pos(0, vert0.vec.x + ORIGIN_X, vert0.vec.y + ORIGIN_Y, vert0.vec.z + ORIGIN_Z)
        emitter.pos(1, vert1.vec.x + ORIGIN_X, vert1.vec.y + ORIGIN_Y, vert1.vec.z + ORIGIN_Z)
        emitter.pos(2, vert2.vec.x + ORIGIN_X, vert2.vec.y + ORIGIN_Y, vert2.vec.z + ORIGIN_Z)
        emitter.pos(3, vert3.vec.x + ORIGIN_X, vert3.vec.y + ORIGIN_Y, vert3.vec.z + ORIGIN_Z)

        emitter.sprite(0, 0, vert0.uv)
        emitter.sprite(1, 0, vert1.uv)
        emitter.sprite(2, 0, vert2.uv)
        emitter.sprite(3, 0, vert3.uv)
    }
}