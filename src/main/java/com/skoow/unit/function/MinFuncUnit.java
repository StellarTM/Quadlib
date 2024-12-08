package com.skoow.unit.function;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;

public class MinFuncUnit extends Func2Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of2("min", Unit::min);

	public MinFuncUnit(Unit a, Unit b) {
		super(FACTORY, a, b);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.min(a.get(variables), b.get(variables));
	}
}
