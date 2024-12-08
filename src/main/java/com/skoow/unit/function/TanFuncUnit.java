package com.skoow.unit.function;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;

public class TanFuncUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("tan", Unit::tan);

	public TanFuncUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.tan(a.get(variables));
	}
}
