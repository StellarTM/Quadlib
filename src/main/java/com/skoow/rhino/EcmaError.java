/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

// API class

package com.skoow.rhino;

import java.io.Serial;

/**
 * The class of exceptions raised by the engine as described in
 * ECMA edition 3. See section 15.11.6 in particular.
 */
public class EcmaError extends RhinoException {
	@Serial
	private static final long serialVersionUID = -6261226256957286699L;

	private final String errorName;
	private final String errorMessage;

	/**
	 * Create an exception with the specified detail message.
	 * <p>
	 * Errors internal to the JavaScript engine will simply throw a
	 * RuntimeException.
	 *
	 * @param sourceName   the name of the source responsible for the error
	 * @param lineNumber   the line number of the source
	 * @param columnNumber the columnNumber of the source (may be zero if
	 *                     unknown)
	 * @param lineSource   the source of the line containing the error (may be
	 *                     null if unknown)
	 */
	EcmaError(Context cx, String errorName, String errorMessage, String sourceName, int lineNumber, String lineSource, int columnNumber) {
		super(cx);
		recordErrorOrigin(sourceName, lineNumber, lineSource, columnNumber);
		this.errorName = errorName;
		this.errorMessage = errorMessage;
	}

	@Override
	public String details() {
		return errorName + ": " + errorMessage;
	}

	/**
	 * Gets the name of the error.
	 * <p>
	 * ECMA edition 3 defines the following
	 * errors: EvalError, RangeError, ReferenceError,
	 * SyntaxError, TypeError, and URIError. Additional error names
	 * may be added in the future.
	 * <p>
	 * See ECMA edition 3, 15.11.7.9.
	 *
	 * @return the name of the error.
	 */
	public String getName() {
		return errorName;
	}

	/**
	 * Gets the message corresponding to the error.
	 * <p>
	 * See ECMA edition 3, 15.11.7.10.
	 *
	 * @return an implementation-defined string describing the error.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
