package com.github.hotm

import com.github.hotm.command.RetrogenPortalCommand
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback

object HotMCommands {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _ ->
            RetrogenPortalCommand.register(dispatcher)
        })
    }
}