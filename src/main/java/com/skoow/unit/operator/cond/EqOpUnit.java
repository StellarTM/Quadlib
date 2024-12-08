package com.skoow.unit.operator.cond;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;
import com.skoow.unit.token.UnitSymbol;

public class EqOpUnit extends CondOpUnit {
	public EqOpUnit(Unit left, Unit right) {
		super(UnitSymbol.EQ, left, right);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left == right || Math.abs(left.get(variables) - right.get(variables)) < 0.00001D;
	}
}
