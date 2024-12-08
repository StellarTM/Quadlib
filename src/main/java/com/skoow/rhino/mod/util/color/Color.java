package com.skoow.rhino.mod.util.color;

import com.skoow.rhino.mod.wrapper.ColorWrapper;
import com.skoow.rhino.util.SpecialEquality;
import net.minecraft.network.chat.TextColor;

public interface Color extends SpecialEquality {
	int getArgbJS();

	default int getRgbJS() {
		return getArgbJS() & 0xFFFFFF;
	}

	default int getFireworkColorJS() {
		return getRgbJS();
	}

	default String getHexJS() {
		return String.format("#%08X", getArgbJS());
	}

	default String getSerializeJS() {
		return getHexJS();
	}

	default TextColor createTextColorJS() {
		return TextColor.fromRgb(getRgbJS());
	}

	@Override
	default boolean specialEquals(Object o, boolean shallow) {
		Color c = ColorWrapper.of(o);
		return shallow ? (getArgbJS() == c.getArgbJS()) : (getRgbJS() == c.getRgbJS());
	}
}