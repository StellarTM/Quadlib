package com.skoow.unit.function;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;

public class SinFuncUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("sin", Unit::sin);

	public SinFuncUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.sin(a.get(variables));
	}
}
