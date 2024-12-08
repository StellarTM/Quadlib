package com.skoow.rhino.mod.util;

import com.skoow.rhino.Context;
import com.skoow.rhino.NativeJavaList;
import com.skoow.rhino.Scriptable;
import com.skoow.rhino.util.CustomJavaToJsWrapper;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.Tag;

public record CollectionTagWrapper(CollectionTag<?> tag) implements CustomJavaToJsWrapper {
	@Override
	public Scriptable convertJavaToJs(Context cx, Scriptable scope, Class<?> staticType) {
		return new NativeJavaList(cx, scope, tag, tag, Tag.class, NBTUtils.VALUE_UNWRAPPER);
	}
}
