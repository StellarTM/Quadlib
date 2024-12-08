package com.skoow.rhino.mod.util;

import com.google.gson.JsonElement;
import com.skoow.rhino.util.RemapForJS;

/**
 * @author LatvianModder
 */
public interface JsonSerializable {
	@RemapForJS("toJson")
	JsonElement toJsonJS();
}