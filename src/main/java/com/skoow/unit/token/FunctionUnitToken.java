package com.skoow.unit.token;

import com.skoow.unit.Unit;
import com.skoow.unit.function.FunctionFactory;

import java.util.List;

public record FunctionUnitToken(String name, List<UnitToken> args) implements UnitToken {
	@Override
	public Unit interpret(UnitTokenStream stream) {
		FunctionFactory factory = stream.context.getFunctionFactory(name);

		if (factory == null) {
			throw new IllegalStateException("Unknown function '" + name + "'!");
		} else if (args.isEmpty()) {
			return factory.create(Unit.EMPTY_ARRAY);
		}

		Unit[] newArgs = new Unit[args.size()];

		for (int i = 0; i < args.size(); i++) {
			newArgs[i] = args.get(i).interpret(stream);
		}

		return factory.create(newArgs);
	}
}
