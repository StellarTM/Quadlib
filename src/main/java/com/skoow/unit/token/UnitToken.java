package com.skoow.unit.token;

import com.skoow.unit.Unit;

import java.util.Stack;

public interface UnitToken {
	default Unit interpret(UnitTokenStream stream) {
		return (Unit) this;
	}

	default boolean nextUnaryOperator() {
		return false;
	}

	default void unstack(Stack<UnitToken> resultStack) {
		resultStack.push(this);
	}
}
