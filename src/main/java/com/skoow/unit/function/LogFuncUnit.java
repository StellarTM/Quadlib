package com.skoow.unit.function;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;

public class LogFuncUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("log", Unit::log);

	public LogFuncUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.log(a.get(variables));
	}
}
