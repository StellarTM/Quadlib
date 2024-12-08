package com.skoow.unit.operator;

import com.skoow.unit.Unit;

@FunctionalInterface
public interface UnaryOperatorFactory {
	Unit create(Unit unit);
}
