package com.skoow.quadlib.common.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public interface Packet {
    default void save(FriendlyByteBuf buf) {

    };
    default void handle(NetworkEvent.Context ctx, ServerPlayer sender) {

    };

    default void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> this::doOnClient);
            handle(ctx.get(),ctx.get().getSender());
        });
        ctx.get().setPacketHandled(true);
    }
    @OnlyIn(Dist.CLIENT)
    default void doOnClient() {

    };
}
