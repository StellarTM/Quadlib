package com.skoow.quadlib.utilities;

import net.minecraft.client.gui.GuiGraphics;

public class Scissors {
    static GuiGraphics local;
    public static void on(GuiGraphics g, int x, int y, int w, int h) {
        local = g;
        local.enableScissor(x,y,x+w,y+h);
    }
    public static void off() {
        local.disableScissor();
    }
}
