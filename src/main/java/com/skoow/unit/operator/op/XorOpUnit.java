package com.skoow.unit.operator.op;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;
import com.skoow.unit.operator.OpUnit;
import com.skoow.unit.token.UnitSymbol;

public class XorOpUnit extends OpUnit {
	public XorOpUnit(Unit left, Unit right) {
		super(UnitSymbol.XOR, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return getInt(variables);
	}

	@Override
	public int getInt(UnitVariables variables) {
		return left.getInt(variables) ^ right.getInt(variables);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left.getBoolean(variables) ^ right.getBoolean(variables);
	}
}
