package com.skoow.unit.function;

import com.skoow.unit.Unit;

public abstract class Func2Unit extends FuncUnit {
	public final Unit a, b;

	public Func2Unit(FunctionFactory factory, Unit a, Unit b) {
		super(factory);
		this.a = a;
		this.b = b;
	}

	@Override
	protected Unit[] getArguments() {
		return new Unit[]{a, b};
	}
}
