package com.skoow.unit.function;

import com.skoow.unit.Unit;

public abstract class Func1Unit extends FuncUnit {
	public final Unit a;

	public Func1Unit(FunctionFactory factory, Unit a) {
		super(factory);
		this.a = a;
	}

	@Override
	protected final Unit[] getArguments() {
		return new Unit[]{a};
	}
}
