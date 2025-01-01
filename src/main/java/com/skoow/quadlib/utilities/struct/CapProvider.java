package com.skoow.quadlib.utilities.struct;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public interface CapProvider extends ICapabilityProvider, INBTSerializable<CompoundTag> {
}
