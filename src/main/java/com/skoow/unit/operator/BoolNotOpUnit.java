package com.skoow.unit.operator;

import com.skoow.unit.Unit;
import com.skoow.unit.UnitVariables;
import com.skoow.unit.token.UnitSymbol;

public class BoolNotOpUnit extends UnaryOpUnit {
	public BoolNotOpUnit(Unit unit) {
		super(UnitSymbol.BOOL_NOT, unit);
	}

	@Override
	public double get(UnitVariables variables) {
		return getBoolean(variables) ? 1.0D : 0.0D;
	}

	@Override
	public int getInt(UnitVariables variables) {
		return getBoolean(variables) ? 1 : 0;
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return !unit.getBoolean(variables);
	}
}
