package com.skoow.unit.function;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;

public class WithAlphaFuncUnit extends Func2Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of2("withAlpha", Unit::withAlpha);

	public WithAlphaFuncUnit(Unit a, Unit b) {
		super(FACTORY, a, b);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.atan2(a.get(variables), b.get(variables));
	}
}
