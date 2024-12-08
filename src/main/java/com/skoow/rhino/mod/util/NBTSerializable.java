package com.skoow.rhino.mod.util;

import com.skoow.rhino.util.RemapForJS;
import net.minecraft.nbt.Tag;

/**
 * @author LatvianModder
 */
public interface NBTSerializable {
	@RemapForJS("toNBT")
	Tag toNBTJS();
}