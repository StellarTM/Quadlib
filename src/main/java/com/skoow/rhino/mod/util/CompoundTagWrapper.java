package com.skoow.rhino.mod.util;

import com.skoow.rhino.Context;
import com.skoow.rhino.NativeJavaMap;
import com.skoow.rhino.Scriptable;
import com.skoow.rhino.util.CustomJavaToJsWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public record CompoundTagWrapper(CompoundTag tag) implements CustomJavaToJsWrapper {
	@Override
	public Scriptable convertJavaToJs(Context cx, Scriptable scope, Class<?> staticType) {
		return new NativeJavaMap(cx, scope, tag, NBTUtils.accessTagMap(tag), Tag.class, NBTUtils.VALUE_UNWRAPPER);
	}
}
