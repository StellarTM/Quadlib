package com.skoow.unit.operator.op;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;
import com.skoow.unit.operator.OpUnit;
import com.skoow.unit.token.UnitSymbol;

public class MulOpUnit extends OpUnit {
	public MulOpUnit(Unit left, Unit right) {
		super(UnitSymbol.MUL, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return left.get(variables) * right.get(variables);
	}
}
