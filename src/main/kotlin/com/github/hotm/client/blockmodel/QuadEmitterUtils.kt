package com.github.hotm.client.blockmodel

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vector4f
import kotlin.math.abs

object QuadEmitterUtils {
    const val ORIGIN_X = 0.5f
    const val ORIGIN_Y = 0.5f
    const val ORIGIN_Z = 0.5f

    fun square(
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

        val vec0: Vector4f
        val vec1: Vector4f
        val vec2: Vector4f
        val vec3: Vector4f

        when (nominalFace) {
            Direction.DOWN -> {
                vec0 = Vector4f(left - ORIGIN_X, depth - ORIGIN_Y, top - ORIGIN_Z, 1f)
                vec1 = Vector4f(left - ORIGIN_X, depth - ORIGIN_Y, bottom - ORIGIN_Z, 1f)
                vec2 = Vector4f(right - ORIGIN_X, depth - ORIGIN_Y, bottom - ORIGIN_Z, 1f)
                vec3 = Vector4f(right - ORIGIN_X, depth - ORIGIN_Y, top - ORIGIN_Z, 1f)
            }
            Direction.UP -> {
                vec0 = Vector4f(left - ORIGIN_X, 1f - depth - ORIGIN_Y, 1f - top - ORIGIN_Z, 1f)
                vec1 = Vector4f(left - ORIGIN_X, 1f - depth - ORIGIN_Y, 1f - bottom - ORIGIN_Z, 1f)
                vec2 = Vector4f(right - ORIGIN_X, 1f - depth - ORIGIN_Y, 1f - bottom - ORIGIN_Z, 1f)
                vec3 = Vector4f(right - ORIGIN_X, 1f - depth - ORIGIN_Y, 1f - top - ORIGIN_Z, 1f)
            }
            Direction.NORTH -> {
                vec0 = Vector4f(right - ORIGIN_X, top - ORIGIN_Y, depth - ORIGIN_Z, 1f)
                vec1 = Vector4f(right - ORIGIN_X, bottom - ORIGIN_Y, depth - ORIGIN_Z, 1f)
                vec2 = Vector4f(left - ORIGIN_X, bottom - ORIGIN_Y, depth - ORIGIN_Z, 1f)
                vec3 = Vector4f(left - ORIGIN_X, top - ORIGIN_Y, depth - ORIGIN_Z, 1f)
            }
            Direction.SOUTH -> {
                vec0 = Vector4f(1f - right - ORIGIN_X, top - ORIGIN_Y, 1f - depth - ORIGIN_Z, 1f)
                vec1 = Vector4f(1f - right - ORIGIN_X, bottom - ORIGIN_Y, 1f - depth - ORIGIN_Z, 1f)
                vec2 = Vector4f(1f - left - ORIGIN_X, bottom - ORIGIN_Y, 1f - depth - ORIGIN_Z, 1f)
                vec3 = Vector4f(1f - left - ORIGIN_X, top - ORIGIN_Y, 1f - depth - ORIGIN_Z, 1f)
            }
            Direction.WEST -> {
                vec0 = Vector4f(depth - ORIGIN_X, top - ORIGIN_Y, left - ORIGIN_Z, 1f)
                vec1 = Vector4f(depth - ORIGIN_X, bottom - ORIGIN_Y, left - ORIGIN_Z, 1f)
                vec2 = Vector4f(depth - ORIGIN_X, bottom - ORIGIN_Y, right - ORIGIN_Z, 1f)
                vec3 = Vector4f(depth - ORIGIN_X, top - ORIGIN_Y, right - ORIGIN_Z, 1f)
            }
            Direction.EAST -> {
                vec0 = Vector4f(1f - depth - ORIGIN_X, top - ORIGIN_Y, 1f - left - ORIGIN_Z, 1f)
                vec1 = Vector4f(1f - depth - ORIGIN_X, bottom - ORIGIN_Y, 1f - left - ORIGIN_Z, 1f)
                vec2 = Vector4f(1f - depth - ORIGIN_X, bottom - ORIGIN_Y, 1f - right - ORIGIN_Z, 1f)
                vec3 = Vector4f(1f - depth - ORIGIN_X, top - ORIGIN_Y, 1f - right - ORIGIN_Z, 1f)
            }
        }

        vec0.transform(rotationContainer.rotation.matrix)
        vec1.transform(rotationContainer.rotation.matrix)
        vec2.transform(rotationContainer.rotation.matrix)
        vec3.transform(rotationContainer.rotation.matrix)

        emitter.pos(0, vec0.x + ORIGIN_X, vec0.y + ORIGIN_Y, vec0.z + ORIGIN_Z)
        emitter.pos(1, vec1.x + ORIGIN_X, vec1.y + ORIGIN_Y, vec1.z + ORIGIN_Z)
        emitter.pos(2, vec2.x + ORIGIN_X, vec2.y + ORIGIN_Y, vec2.z + ORIGIN_Z)
        emitter.pos(3, vec3.x + ORIGIN_X, vec3.y + ORIGIN_Y, vec3.z + ORIGIN_Z)
    }
}