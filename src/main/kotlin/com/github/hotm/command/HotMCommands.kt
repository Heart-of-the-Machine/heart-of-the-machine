package com.github.hotm.command

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback

object HotMCommands {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            RetrogenPortalCommand.register(dispatcher)
            BaseAuraCommand.register(dispatcher)
        })
    }
}
