package com.skoow.unit.function;

import com.skoow.unit.UnitVariables;

public class RoundedTimeUnit extends FuncUnit {
	public static long time() {
		return Math.round(System.currentTimeMillis() / 1000D);
	}

	public static final FunctionFactory FACTORY = FunctionFactory.of0("roundedTime", RoundedTimeUnit::new);

	private RoundedTimeUnit() {
		super(FACTORY);
	}

	@Override
	public double get(UnitVariables variables) {
		return time();
	}


}