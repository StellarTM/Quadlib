package com.skoow.unit.function;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;

public class ClampFuncUnit extends Func3Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of3("clamp", Unit::clamp);

	public static double clamp(double a, double b, double c) {
		if (a < b) {
			return b;
		} else {
			return a > c ? c : a;
		}
	}

	public ClampFuncUnit(Unit a, Unit b, Unit c) {
		super(FACTORY, a, b, c);
	}

	@Override
	public double get(UnitVariables variables) {
		return clamp(a.get(variables), b.get(variables), c.get(variables));
	}
}
