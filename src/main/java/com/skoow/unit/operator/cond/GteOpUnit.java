package com.skoow.unit.operator.cond;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;
import com.skoow.unit.token.UnitSymbol;

public class GteOpUnit extends CondOpUnit {
	public GteOpUnit(Unit left, Unit right) {
		super(UnitSymbol.GTE, left, right);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left.get(variables) >= right.get(variables);
	}
}
