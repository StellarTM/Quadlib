package com.skoow.unit.function;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;

public class Log10FuncUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("log10", Unit::log10);

	public Log10FuncUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.log10(a.get(variables));
	}
}
