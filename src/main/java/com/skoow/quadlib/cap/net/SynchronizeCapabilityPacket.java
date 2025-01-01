package com.skoow.quadlib.cap.net;

import com.skoow.quadlib.cap.CapEntry;
import com.skoow.quadlib.cap.CapManager;
import com.skoow.quadlib.common.net.Packet;
import com.skoow.quadlib.utilities.MCUtil;
import com.skoow.quadlib.utilities.struct.CapImpl;
import com.skoow.quadlib.utilities.struct.Structs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.capabilities.Capability;

public class SynchronizeCapabilityPacket implements Packet {
    CompoundTag nbt;
    int id;
    public SynchronizeCapabilityPacket(CapImpl impl, int id) {
        this(impl.serializeNBT(),id);
    }
    public SynchronizeCapabilityPacket(CompoundTag tag,int id) {
        nbt = tag;
        this.id = id;
    }
    public SynchronizeCapabilityPacket(FriendlyByteBuf buf) {
        id = buf.readInt();
        nbt = buf.readNbt();
    }
    @Override
    public void save(FriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeNbt(nbt);
    }

    @Override
    public void doOnClient() {
        Structs.safeRun(MCUtil.player(),p -> {
            CapEntry<?> entry = CapManager.caps.get(id);
            Capability<?> inst = entry.instance.get();
            p.getCapability(inst).ifPresent(a -> {
                if(a instanceof CapImpl i) i.deserializeNBT(nbt);
            });
        });
    }
}
