package com.skoow.quadlib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(Quadlib.ID)
public class Quadlib {
    public static final String ID = "quadlib";

    public Quadlib() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
