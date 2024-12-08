package com.skoow.rhino.util;

import com.skoow.rhino.Context;
import com.skoow.rhino.IdEnumerationIterator;
import com.skoow.rhino.JavaScriptException;

import java.util.Iterator;
import java.util.function.Consumer;

public record JavaIteratorWrapper(Iterator<?> parent) implements IdEnumerationIterator {
	@Override
	public boolean enumerationIteratorHasNext(Context cx, Consumer<Object> callback) {
		if (parent.hasNext()) {
			callback.accept(parent.next());
			return true;
		}

		return false;
	}

	@Override
	public boolean enumerationIteratorNext(Context cx, Consumer<Object> callback) throws JavaScriptException {
		if (parent.hasNext()) {
			callback.accept(parent.next());
			return true;
		}

		return false;
	}
}
