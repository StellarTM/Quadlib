package com.skoow.unit.operator;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;
import com.skoow.unit.VariableUnit;
import com.skoow.unit.token.UnitSymbol;

public class SetUnit extends OpUnit {
	public SetUnit(UnitSymbol symbol, Unit left, Unit right) {
		super(symbol, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		if (left instanceof VariableUnit var) {
			variables.getVariables().set(var.name, right.get(variables));
		}

		return right.get(variables);
	}

	@Override
	public int getInt(UnitVariables variables) {
		if (left instanceof VariableUnit var) {
			variables.getVariables().set(var.name, right.get(variables));
		}

		return right.getInt(variables);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		if (left instanceof VariableUnit var) {
			variables.getVariables().set(var.name, right.get(variables));
		}

		return right.getBoolean(variables);
	}
}
