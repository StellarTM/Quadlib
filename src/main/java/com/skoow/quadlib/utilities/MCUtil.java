package com.skoow.quadlib.utilities;

import com.skoow.quadlib.utilities.func.Cons;
import com.skoow.quadlib.utilities.struct.Seq;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

public class MCUtil {
    public static Minecraft mc() {
        return Minecraft.getInstance();
    }
    public static MinecraftServer server() {
        return ServerLifecycleHooks.getCurrentServer();
    }
    public static ServerLevel overworld() {
        return server().overworld();
    }
    public static LocalPlayer player() {
        return mc().player;
    }
    public static Seq<ServerPlayer> players() {
        return Seq.with(server().getPlayerList().getPlayers());
    }
    public static void player(Cons<ServerPlayer> cons) {
        players().each(cons);
    }
    public static CommandSourceStack cmdSource() {
        return server().createCommandSourceStack();
    }
}
