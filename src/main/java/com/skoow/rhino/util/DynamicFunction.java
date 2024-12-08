package com.skoow.rhino.util;

import com.skoow.rhino.BaseFunction;
import com.skoow.rhino.Context;
import com.skoow.rhino.Scriptable;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class DynamicFunction extends BaseFunction {
	private final Callback function;

	public DynamicFunction(Callback f) {
		function = f;
	}

	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		return function.call(args);
	}

	@FunctionalInterface
	public interface Callback {
		@Nullable
		Object call(Object[] args);
	}
}