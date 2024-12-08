package com.skoow.unit.operator.op;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;
import com.skoow.unit.operator.OpUnit;
import com.skoow.unit.token.UnitSymbol;

public class PowOpUnit extends OpUnit {
	public PowOpUnit(Unit left, Unit right) {
		super(UnitSymbol.POW, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.pow(left.get(variables), right.get(variables));
	}
}
