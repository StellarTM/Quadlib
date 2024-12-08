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
import com.skoow.unit.function.FunctionFactory;
import com.skoow.unit.function.HsvFuncUnit;
import com.skoow.unit.function.LerpFuncUnit;
import com.skoow.unit.function.Log10FuncUnit;
import com.skoow.unit.function.Log1pFuncUnit;
import com.skoow.unit.function.LogFuncUnit;
import com.skoow.unit.function.MapFuncUnit;
import com.skoow.unit.function.MaxFuncUnit;
import com.skoow.unit.function.MinFuncUnit;
import com.skoow.unit.function.RadFuncUnit;
import com.skoow.unit.function.RandomUnit;
import com.skoow.unit.function.RgbFuncUnit;
import com.skoow.unit.function.RoundedTimeUnit;
import com.skoow.unit.function.SinFuncUnit;
import com.skoow.unit.function.SmoothstepFuncUnit;
import com.skoow.unit.function.SqFuncUnit;
import com.skoow.unit.function.SqrtFuncUnit;
import com.skoow.unit.function.TanFuncUnit;
import com.skoow.unit.function.TimeUnit;
import com.skoow.unit.function.WithAlphaFuncUnit;
import com.skoow.unit.token.UnitTokenStream;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UnitContext {
	public static final UnitContext DEFAULT = new UnitContext();

	static {
		DEFAULT.addFunction(TimeUnit.FACTORY);
		DEFAULT.addFunction(RoundedTimeUnit.FACTORY);
		DEFAULT.addFunction(RandomUnit.FACTORY);
		DEFAULT.addFunction(FunctionFactory.of3("if", TernaryUnit::new));
		DEFAULT.addFunction(RgbFuncUnit.FACTORY);

		DEFAULT.addFunction(MinFuncUnit.FACTORY);
		DEFAULT.addFunction(MaxFuncUnit.FACTORY);
		DEFAULT.addFunction(AbsFuncUnit.FACTORY);
		DEFAULT.addFunction(SinFuncUnit.FACTORY);
		DEFAULT.addFunction(CosFuncUnit.FACTORY);
		DEFAULT.addFunction(TanFuncUnit.FACTORY);
		DEFAULT.addFunction(DegFuncUnit.FACTORY);
		DEFAULT.addFunction(RadFuncUnit.FACTORY);
		DEFAULT.addFunction(AtanFuncUnit.FACTORY);
		DEFAULT.addFunction(Atan2FuncUnit.FACTORY);
		DEFAULT.addFunction(LogFuncUnit.FACTORY);
		DEFAULT.addFunction(Log10FuncUnit.FACTORY);
		DEFAULT.addFunction(Log1pFuncUnit.FACTORY);
		DEFAULT.addFunction(SqrtFuncUnit.FACTORY);
		DEFAULT.addFunction(SqFuncUnit.FACTORY);
		DEFAULT.addFunction(FloorFuncUnit.FACTORY);
		DEFAULT.addFunction(CeilFuncUnit.FACTORY);
		DEFAULT.addFunction(BoolFuncUnit.FACTORY);
		DEFAULT.addFunction(ClampFuncUnit.FACTORY);
		DEFAULT.addFunction(LerpFuncUnit.FACTORY);
		DEFAULT.addFunction(SmoothstepFuncUnit.FACTORY);
		DEFAULT.addFunction(HsvFuncUnit.FACTORY);
		DEFAULT.addFunction(WithAlphaFuncUnit.FACTORY);
		DEFAULT.addFunction(MapFuncUnit.FACTORY);

		DEFAULT.addConstant("true", FixedBooleanUnit.TRUE);
		DEFAULT.addConstant("false", FixedBooleanUnit.FALSE);
		DEFAULT.addConstant("PI", FixedNumberUnit.PI);
		DEFAULT.addConstant("TWO_PI", FixedNumberUnit.TWO_PI);
		DEFAULT.addConstant("HALF_PI", FixedNumberUnit.HALF_PI);
		DEFAULT.addConstant("E", FixedNumberUnit.E);
	}

	public final Map<String, Unit> constants = new HashMap<>();
	private final Map<String, FunctionFactory> functions = new HashMap<>();
	private final Map<String, Unit> cache = new HashMap<>();
	private int debug = -1;

	public void addFunction(FunctionFactory factory) {
		functions.put(factory.name(), factory);
	}

	@Nullable
	public FunctionFactory getFunctionFactory(String name) {
		return functions.get(name);
	}

	public void addConstant(String s, Unit u) {
		constants.put(s, u);
	}

	public UnitContext sub() {
		UnitContext ctx = new UnitContext();
		ctx.functions.putAll(functions);
		ctx.debug = debug;
		return ctx;
	}

	public UnitTokenStream createStream(String input) {
		return new UnitTokenStream(this, input);
	}

	public Unit parse(String input) {
		Unit u = cache.get(input);

		if (u == null) {
			u = createStream(input).getUnit();
			cache.put(input, u);
		}

		return u;
	}

	public boolean isDebug() {
		return debug >= 0;
	}

	public void pushDebug() {
		debug++;
	}

	public void popDebug() {
		debug--;
	}

	public void debugInfo(String s) {
		if (debug >= 0) {
			if (debug >= 2) {
				System.out.println("  ".repeat(debug - 1) + s);
			} else {
				System.out.println(s);
			}
		}
	}

	public void debugInfo(String s, Collection<?> values) {
		debugInfo(s + ": " + values.stream().map(Object::toString).collect(Collectors.joining("  ")));
	}
}
