package com.skoow.rhino.util;

import com.skoow.rhino.Context;
import com.skoow.rhino.Scriptable;

@FunctionalInterface
public interface ValueUnwrapper {
	ValueUnwrapper DEFAULT = (cx, scope, value) -> cx.getWrapFactory().wrap(cx, scope, value, value.getClass());

	Object unwrap(Context cx, Scriptable scope, Object value);
}
