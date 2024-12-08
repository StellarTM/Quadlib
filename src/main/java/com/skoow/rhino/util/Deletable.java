package com.skoow.rhino.util;

import com.skoow.rhino.Wrapper;

public interface Deletable {
	static void deleteObject(Object o) {
		Object o1 = Wrapper.unwrapped(o);

		if (o1 instanceof Deletable) {
			((Deletable) o1).onDeletedByJS();
		}
	}

	void onDeletedByJS();
}
