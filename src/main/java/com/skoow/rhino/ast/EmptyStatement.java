/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.skoow.rhino.ast;

import com.skoow.rhino.Token;

/**
 * AST node for an empty statement.  Node type is {@link Token#EMPTY}.
 */
public class EmptyStatement extends AstNode {

	{
		type = Token.EMPTY;
	}

	public EmptyStatement() {
	}

	public EmptyStatement(int pos) {
		super(pos);
	}

	public EmptyStatement(int pos, int len) {
		super(pos, len);
	}
}
