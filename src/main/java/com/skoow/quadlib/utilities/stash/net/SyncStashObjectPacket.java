package com.skoow.quadlib.utilities.stash.net;

import com.skoow.quadlib.common.net.Packet;
import com.skoow.quadlib.utilities.stash.SyncStash;
import net.minecraft.network.FriendlyByteBuf;

public class SyncStashObjectPacket implements Packet {
    int id;
    byte[] bytes;
    public SyncStashObjectPacket(int id, byte[] bytes) {
        this.id = id;
        this.bytes = bytes;
    }
    public SyncStashObjectPacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        this.bytes = buf.readByteArray();
    }
    @Override
    public void save(FriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeByteArray(bytes);
    }

    @Override
    public void doOnClient() {
        SyncStash.set(id,bytes);
    }
}
