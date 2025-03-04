/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.skoow.rhino;

/**
 * This is the default error reporter for JavaScript.
 *
 * @author Norris Boyd
 */
class DefaultErrorReporter implements ErrorReporter {
	static final DefaultErrorReporter instance = new DefaultErrorReporter();

	static ErrorReporter forEval(ErrorReporter reporter) {
		DefaultErrorReporter r = new DefaultErrorReporter();
		r.forEval = true;
		r.chainedReporter = reporter;
		return r;
	}

	private boolean forEval;
	private ErrorReporter chainedReporter;

	private DefaultErrorReporter() {
	}

	@Override
	public void warning(String message, String sourceURI, int line, String lineText, int lineOffset) {
		if (chainedReporter != null) {
			chainedReporter.warning(message, sourceURI, line, lineText, lineOffset);
		} else {
			// Do nothing
		}
	}

	@Override
	public void error(Context cx, String message, String sourceURI, int line, String lineText, int lineOffset) {
		if (forEval) {
			// Assume error message strings that start with "TypeError: "
			// should become TypeError exceptions. A bit of a hack, but we
			// don't want to change the ErrorReporter interface.
			String error = "SyntaxError";
			final String TYPE_ERROR_NAME = "TypeError";
			final String DELIMETER = ": ";
			final String prefix = TYPE_ERROR_NAME + DELIMETER;
			if (message.startsWith(prefix)) {
				error = TYPE_ERROR_NAME;
				message = message.substring(prefix.length());
			}
			throw ScriptRuntime.constructError(cx, error, message, sourceURI, line, lineText, lineOffset);
		}
		if (chainedReporter != null) {
			chainedReporter.error(cx, message, sourceURI, line, lineText, lineOffset);
		} else {
			throw runtimeError(cx, message, sourceURI, line, lineText, lineOffset);
		}
	}

	@Override
	public EvaluatorException runtimeError(Context cx, String message, String sourceURI, int line, String lineText, int lineOffset) {
		if (chainedReporter != null) {
			return chainedReporter.runtimeError(cx, message, sourceURI, line, lineText, lineOffset);
		}
		return new EvaluatorException(cx, message, sourceURI, line, lineText, lineOffset);
	}
}
