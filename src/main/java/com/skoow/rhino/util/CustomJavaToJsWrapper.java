package com.skoow.rhino.util;

import com.skoow.rhino.Context;
import com.skoow.rhino.Scriptable;

@FunctionalInterface
public interface CustomJavaToJsWrapper {
	Scriptable convertJavaToJs(Context cx, Scriptable scope, Class<?> staticType);
}
