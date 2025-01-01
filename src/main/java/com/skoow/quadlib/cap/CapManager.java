package com.skoow.quadlib.cap;

import com.mojang.logging.LogUtils;
import com.skoow.quadlib.utilities.func.Prov;
import com.skoow.quadlib.utilities.struct.CapImpl;
import com.skoow.quadlib.utilities.struct.CapProvider;
import com.skoow.quadlib.utilities.struct.Seq;
import com.skoow.quadlib.utilities.struct.Var;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.charset.StandardCharsets;

@Mod.EventBusSubscriber()
public class CapManager {

    public static Seq<CapEntry<?>> caps = Seq.with();

    private static Var<String> tempMod = new Var<>("");
    public static void begin(String modId) {
        tempMod.var = modId;
    }
    public static <T> CapEntry<T> reg(String modId, String id, Prov<CapProvider> provider, Prov<Capability<T>> instance) {
        CapEntry<T> entry = new CapEntry<>();
        entry.capId = id;
        entry.modId = modId;
        entry.prov = provider;
        entry.instance = instance;
        entry.id = caps.size;
        if(caps.contains(entry))
            LogUtils.getLogger().warn("Existing capability register: {}:{}", tempMod, id);
        caps.addUnique(entry);
        return entry;
    }
    public static void close(String modId) {
        if(modId.equals(tempMod.var)) tempMod.var = null;
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player)
            for (CapEntry<?> cap : caps)
                event.addCapability(new ResourceLocation(cap.modId,cap.capId), cap.prov.get());
    }


    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        for (CapEntry<?> cap : caps) {
            Capability<?> CAP = cap.instance.get();
            event.getOriginal().reviveCaps();;
            event.getEntity().getCapability(CAP).ifPresent(k -> {
                event.getOriginal().getCapability(CAP).ifPresent(o -> {
                    INBTSerializable<CompoundTag> kSer = (INBTSerializable<CompoundTag>) k;
                    INBTSerializable<CompoundTag> oSer = (INBTSerializable<CompoundTag>) o;
                    kSer.deserializeNBT(oSer.serializeNBT());
                });
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        sync(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        sync(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        sync(event.getEntity());
    }

    public static void sync(Player player) {
        if(player instanceof ServerPlayer s)
            for (CapEntry<?> cap : caps)
                CapUtil.get(player,(CapEntry<CapImpl>) cap, i -> i.sync(s));
    }
}
