package com.skoow.unit.function;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;

public class MaxFuncUnit extends Func2Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of2("max", Unit::max);

	public MaxFuncUnit(Unit a, Unit b) {
		super(FACTORY, a, b);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.max(a.get(variables), b.get(variables));
	}
}
