package com.skoow.unit;

import com.skoow.unit.function.AbsFuncUnit;
import com.skoow.unit.function.Atan2FuncUnit;
import com.skoow.unit.function.AtanFuncUnit;
import com.skoow.unit.function.BoolFuncUnit;
import com.skoow.unit.function.CeilFuncUnit;
import com.skoow.unit.function.ClampFuncUnit;
import com.skoow.unit.function.CosFuncUnit;
import com.skoow.unit.function.DegFuncUnit;
import com.skoow.unit.function.FloorFuncUnit;
import com.skoow.unit.function.LerpFuncUnit;
import com.skoow.unit.function.Log10FuncUnit;
import com.skoow.unit.function.Log1pFuncUnit;
import com.skoow.unit.function.LogFuncUnit;
import com.skoow.unit.function.MaxFuncUnit;
import com.skoow.unit.function.MinFuncUnit;
import com.skoow.unit.function.RadFuncUnit;
import com.skoow.unit.function.SinFuncUnit;
import com.skoow.unit.function.SmoothstepFuncUnit;
import com.skoow.unit.function.SqFuncUnit;
import com.skoow.unit.function.SqrtFuncUnit;
import com.skoow.unit.function.TanFuncUnit;
import com.skoow.unit.function.WithAlphaFuncUnit;
import com.skoow.unit.operator.BitNotOpUnit;
import com.skoow.unit.operator.BoolNotOpUnit;
import com.skoow.unit.operator.NegateOpUnit;
import com.skoow.unit.operator.SetUnit;
import com.skoow.unit.operator.cond.AndOpUnit;
import com.skoow.unit.operator.cond.EqOpUnit;
import com.skoow.unit.operator.cond.GtOpUnit;
import com.skoow.unit.operator.cond.GteOpUnit;
import com.skoow.unit.operator.cond.LtOpUnit;
import com.skoow.unit.operator.cond.LteOpUnit;
import com.skoow.unit.operator.cond.NeqOpUnit;
import com.skoow.unit.operator.cond.OrOpUnit;
import com.skoow.unit.operator.op.AddOpUnit;
import com.skoow.unit.operator.op.BitAndOpUnit;
import com.skoow.unit.operator.op.BitOrOpUnit;
import com.skoow.unit.operator.op.DivOpUnit;
import com.skoow.unit.operator.op.LshOpUnit;
import com.skoow.unit.operator.op.ModOpUnit;
import com.skoow.unit.operator.op.MulOpUnit;
import com.skoow.unit.operator.op.PowOpUnit;
import com.skoow.unit.operator.op.RshOpUnit;
import com.skoow.unit.operator.op.SubOpUnit;
import com.skoow.unit.operator.op.XorOpUnit;
import com.skoow.unit.token.UnitSymbol;

public abstract class Unit {
	public static Unit[] EMPTY_ARRAY = new Unit[0];

	public boolean isFixed() {
		return false;
	}

	public abstract double get(UnitVariables variables);

	public float getFloat(UnitVariables variables) {
		return (float) get(variables);
	}

	public int getInt(UnitVariables variables) {
		double d = get(variables);
		int i = (int) d;
		return d < (double) i ? i - 1 : i;
	}

	public boolean getBoolean(UnitVariables variables) {
		return get(variables) != 0D;
	}

	public void toString(StringBuilder builder) {
		builder.append(this);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	// Operators

	public Unit positive() {
		return this;
	}

	public Unit negate() {
		return new NegateOpUnit(this);
	}

	public Unit add(Unit other) {
		return new AddOpUnit(this, other);
	}

	public Unit add(double value) {
		return add(FixedNumberUnit.of(value));
	}

	public Unit sub(Unit other) {
		return new SubOpUnit(this, other);
	}

	public Unit sub(double value) {
		return sub(FixedNumberUnit.of(value));
	}

	public Unit mul(Unit other) {
		return new MulOpUnit(this, other);
	}

	public Unit mul(double value) {
		return add(FixedNumberUnit.of(value));
	}

	public Unit div(Unit other) {
		return new DivOpUnit(this, other);
	}

	public Unit div(double value) {
		return add(FixedNumberUnit.of(value));
	}

	public Unit mod(Unit other) {
		return new ModOpUnit(this, other);
	}

	public Unit mod(double value) {
		return mod(FixedNumberUnit.of(value));
	}

	public Unit pow(Unit other) {
		return new PowOpUnit(this, other);
	}

	public Unit lsh(Unit other) {
		return new LshOpUnit(this, other);
	}

	public Unit rsh(Unit other) {
		return new RshOpUnit(this, other);
	}

	public Unit bitAnd(Unit other) {
		return new BitAndOpUnit(this, other);
	}

	public Unit bitOr(Unit other) {
		return new BitOrOpUnit(this, other);
	}

	public Unit xor(Unit other) {
		return new XorOpUnit(this, other);
	}

	public Unit bitNot() {
		return new BitNotOpUnit(this);
	}

	public Unit eq(Unit other) {
		return new EqOpUnit(this, other);
	}

	public Unit neq(Unit other) {
		return new NeqOpUnit(this, other);
	}

	public Unit lt(Unit other) {
		return new LtOpUnit(this, other);
	}

	public Unit gt(Unit other) {
		return new GtOpUnit(this, other);
	}

	public Unit lte(Unit other) {
		return new LteOpUnit(this, other);
	}

	public Unit gte(Unit other) {
		return new GteOpUnit(this, other);
	}

	public Unit and(Unit other) {
		return new AndOpUnit(this, other);
	}

	public Unit or(Unit other) {
		return new OrOpUnit(this, other);
	}

	public Unit boolNot() {
		return new BoolNotOpUnit(this);
	}

	// Functions

	public Unit min(Unit other) {
		return new MinFuncUnit(this, other);
	}

	public Unit max(Unit other) {
		return new MaxFuncUnit(this, other);
	}

	public Unit abs() {
		return new AbsFuncUnit(this);
	}

	public Unit sin() {
		return new SinFuncUnit(this);
	}

	public Unit cos() {
		return new CosFuncUnit(this);
	}

	public Unit tan() {
		return new TanFuncUnit(this);
	}

	public Unit deg() {
		return new DegFuncUnit(this);
	}

	public Unit rad() {
		return new RadFuncUnit(this);
	}

	public Unit atan() {
		return new AtanFuncUnit(this);
	}

	public Unit atan2(Unit other) {
		return new Atan2FuncUnit(this, other);
	}

	public Unit log() {
		return new LogFuncUnit(this);
	}

	public Unit log10() {
		return new Log10FuncUnit(this);
	}

	public Unit log1p() {
		return new Log1pFuncUnit(this);
	}

	public Unit sqrt() {
		return new SqrtFuncUnit(this);
	}

	public Unit sq() {
		return new SqFuncUnit(this);
	}

	public Unit floor() {
		return new FloorFuncUnit(this);
	}

	public Unit ceil() {
		return new CeilFuncUnit(this);
	}

	public Unit bool() {
		return new BoolFuncUnit(this);
	}

	public Unit clamp(Unit a, Unit b) {
		return new ClampFuncUnit(this, a, b);
	}

	public Unit lerp(Unit a, Unit b) {
		return new LerpFuncUnit(this, a, b);
	}

	public Unit smoothstep() {
		return new SmoothstepFuncUnit(this);
	}

	public Unit withAlpha(Unit a) {
		return new WithAlphaFuncUnit(this, a);
	}

	public Unit set(Unit unit) {
		return new SetUnit(UnitSymbol.SET, this, unit);
	}

	public Unit addSet(Unit unit) {
		return new SetUnit(UnitSymbol.ADD_SET, this, add(unit));
	}

	public Unit subSet(Unit unit) {
		return new SetUnit(UnitSymbol.SUB_SET, this, sub(unit));
	}

	public Unit mulSet(Unit unit) {
		return new SetUnit(UnitSymbol.MUL_SET, this, mul(unit));
	}

	public Unit divSet(Unit unit) {
		return new SetUnit(UnitSymbol.DIV_SET, this, div(unit));
	}

	public Unit modSet(Unit unit) {
		return new SetUnit(UnitSymbol.MOD_SET, this, mod(unit));
	}
}
