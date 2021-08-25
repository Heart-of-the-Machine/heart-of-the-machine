package com.github.hotm.client.blockmodel.util

import com.github.hotm.client.blockmodel.QuadEmitterUtils
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.util.math.Direction

data class QuadPos(val left: Float, val bottom: Float, val right: Float, val top: Float, val depth: Float) {
    fun emit(emitter: QuadEmitter, face: Direction, rotationContainer: ModelBakeSettings?) {
        if (rotationContainer != null) {
            QuadEmitterUtils.square(emitter, rotationContainer, face, left, bottom, right, top, depth)
        } else {
            emitter.square(face, left, bottom, right, top, depth)
        }
        emitter.sprite(0, 0, left, 1.0f - top)
        emitter.sprite(1, 0, left, 1.0f - bottom)
        emitter.sprite(2, 0, right, 1.0f - bottom)
        emitter.sprite(3, 0, right, 1.0f - top)
    }
}