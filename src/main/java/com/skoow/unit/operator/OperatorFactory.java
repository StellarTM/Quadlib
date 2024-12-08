package com.skoow.unit.operator;

import com.skoow.unit.Unit;

@FunctionalInterface
public interface OperatorFactory {
	Unit create(Unit left, Unit right);
}
