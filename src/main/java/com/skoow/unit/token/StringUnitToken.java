package com.skoow.unit.token;

import com.skoow.unit.FixedNumberUnit;
import com.skoow.unit.Unit;
import com.skoow.unit.VariableUnit;

public record StringUnitToken(String name) implements UnitToken {
	@Override
	public Unit interpret(UnitTokenStream stream) {
		var constant = stream.context.constants.get(name);

		if (constant != null) {
			return constant;
		}

		try {
			return FixedNumberUnit.of(Double.parseDouble(name));
		} catch (Exception ex) {
			return VariableUnit.of(name);
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
