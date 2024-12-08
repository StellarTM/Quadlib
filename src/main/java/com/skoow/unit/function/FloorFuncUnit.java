package com.skoow.unit.function;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;

public class FloorFuncUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("floor", Unit::floor);

	public FloorFuncUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.floor(a.get(variables));
	}
}
