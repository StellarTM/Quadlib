package com.skoow.unit.operator.op;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;
import com.skoow.unit.operator.OpUnit;
import com.skoow.unit.token.UnitSymbol;

public class DivOpUnit extends OpUnit {
	public DivOpUnit(Unit left, Unit right) {
		super(UnitSymbol.DIV, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return left.get(variables) / right.get(variables);
	}
}
