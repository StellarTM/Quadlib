package com.skoow.unit.function;

import com.skoow.unit.UnitVariables;

public class TimeUnit extends FuncUnit {
	public static double time() {
		return System.currentTimeMillis() / 1000D;
	}

	public static final FunctionFactory FACTORY = FunctionFactory.of0("time", TimeUnit::new);

	private TimeUnit() {
		super(FACTORY);
	}

	@Override
	public double get(UnitVariables variables) {
		return time();
	}


}