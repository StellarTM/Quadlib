package com.skoow.unit.function;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;

public class RadFuncUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("rad", Unit::rad);

	public RadFuncUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.toRadians(a.get(variables));
	}
}
