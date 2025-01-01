package com.skoow.quadlib.utilities.struct;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;

public interface CapImpl extends INBTSerializable<CompoundTag> {
    void sync(ServerPlayer player);
}
