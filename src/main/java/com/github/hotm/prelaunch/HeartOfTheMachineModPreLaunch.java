package com.github.hotm.prelaunch;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import java.lang.reflect.InvocationTargetException;

public class HeartOfTheMachineModPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        try {
            PreLaunchHacks.hackilyLoadForMixin("com.mojang.datafixers.kinds.App");
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
