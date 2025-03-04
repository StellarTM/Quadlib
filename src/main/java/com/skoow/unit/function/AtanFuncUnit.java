package com.skoow.unit.function;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;

public class AtanFuncUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("atan", Unit::atan);

	public AtanFuncUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.atan(a.get(variables));
	}
}
