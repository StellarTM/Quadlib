package com.skoow.unit.operator.op;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;
import com.skoow.unit.operator.OpUnit;
import com.skoow.unit.token.UnitSymbol;

public class AddOpUnit extends OpUnit {
	public AddOpUnit(Unit left, Unit right) {
		super(UnitSymbol.ADD, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return left.get(variables) + right.get(variables);
	}
}
