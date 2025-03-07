/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.skoow.rhino;

/**
 * This class implements the activation object.
 * <p>
 * See ECMA 10.1.6
 *
 * @author Norris Boyd
 * @see Arguments
 */
public final class NativeCall extends IdScriptableObject {
	private static final Object CALL_TAG = "Call";
	private static final int Id_constructor = 1;
	private static final int MAX_PROTOTYPE_ID = 1;

	static void init(Scriptable scope, boolean sealed, Context cx) {
		NativeCall obj = new NativeCall();
		obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed, cx);
	}

	NativeFunction function;
	Object[] originalArgs;
	boolean isStrict;
	transient NativeCall parentActivationCall;
	private Arguments arguments;

	NativeCall() {
	}

	NativeCall(NativeFunction function, Scriptable scope, Object[] args, boolean isArrow, boolean isStrict, Context cx) {
		this.function = function;

		setParentScope(scope);
		// leave prototype null

		this.originalArgs = (args == null) ? ScriptRuntime.EMPTY_OBJECTS : args;
		this.isStrict = isStrict;

		// initialize values of arguments
		int paramAndVarCount = function.getParamAndVarCount();
		int paramCount = function.getParamCount();
		if (paramAndVarCount != 0) {
			for (int i = 0; i < paramCount; ++i) {
				String name = function.getParamOrVarName(i);
				Object val = i < args.length ? args[i] : Undefined.instance;
				defineProperty(cx, name, val, PERMANENT);
			}
		}

		// initialize "arguments" property but only if it was not overridden by
		// the parameter with the same name
		if (!super.has(cx, "arguments", this) && !isArrow) {
			arguments = new Arguments(this, cx);
			defineProperty(cx, "arguments", arguments, PERMANENT);
		}

		if (paramAndVarCount != 0) {
			for (int i = paramCount; i < paramAndVarCount; ++i) {
				String name = function.getParamOrVarName(i);
				if (!super.has(cx, name, this)) {
					if (function.getParamOrVarConst(i)) {
						defineProperty(cx, name, Undefined.instance, CONST);
					} else if (!(function instanceof InterpretedFunction) || ((InterpretedFunction) function).hasFunctionNamed(name)) {
						defineProperty(cx, name, Undefined.instance, PERMANENT);
					}
				}
			}
		}
	}

	@Override
	public String getClassName() {
		return "Call";
	}

	@Override
	protected int findPrototypeId(String s) {
		return s.equals("constructor") ? Id_constructor : 0;
	}

	@Override
	protected void initPrototypeId(int id, Context cx) {
		String s;
		int arity;
		if (id == Id_constructor) {
			arity = 1;
			s = "constructor";
		} else {
			throw new IllegalArgumentException(String.valueOf(id));
		}
		initPrototypeMethod(CALL_TAG, id, s, arity, cx);
	}

	@Override
	public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		if (!f.hasTag(CALL_TAG)) {
			return super.execIdCall(f, cx, scope, thisObj, args);
		}
		int id = f.methodId();
		if (id == Id_constructor) {
			if (thisObj != null) {
				throw Context.reportRuntimeError1("msg.only.from.new", "Call", cx);
			}
			ScriptRuntime.checkDeprecated(cx, "Call");
			NativeCall result = new NativeCall();
			result.setPrototype(getObjectPrototype(scope, cx));
			return result;
		}
		throw new IllegalArgumentException(String.valueOf(id));
	}

	public void defineAttributesForArguments(Context cx) {
		if (arguments != null) {
			arguments.defineAttributesForStrictMode(cx);
		}
	}
}

