package com.skoow.rhino.mod.util;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface ChangeListener<T> {
	void onChanged(T o);
}