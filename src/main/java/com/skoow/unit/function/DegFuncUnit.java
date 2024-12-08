package com.skoow.unit.function;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;

public class DegFuncUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("deg", Unit::deg);

	public DegFuncUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.toDegrees(a.get(variables));
	}
}
