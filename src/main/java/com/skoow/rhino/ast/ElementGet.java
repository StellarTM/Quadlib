/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.skoow.rhino.ast;

import com.skoow.rhino.Token;

/**
 * AST node for an indexed property reference, such as {@code foo['bar']} or
 * {@code foo[2]}.  This is sometimes called an "element-get" operation, hence
 * the name of the node.<p>
 * <p>
 * Node type is {@link Token#GETELEM}.<p>
 * <p>
 * The node bounds extend from the beginning position of the target through the
 * closing right-bracket.  In the presence of a syntax error, the right bracket
 * position is -1, and the node ends at the end of the element expression.
 */
public class ElementGet extends AstNode {

	private AstNode target;
	private AstNode element;
	private int lb = -1;
	private int rb = -1;

	{
		type = Token.GETELEM;
	}

	public ElementGet() {
	}

	public ElementGet(int pos) {
		super(pos);
	}

	public ElementGet(int pos, int len) {
		super(pos, len);
	}

	public ElementGet(AstNode target, AstNode element) {
		setTarget(target);
		setElement(element);
	}

	/**
	 * Returns the object on which the element is being fetched.
	 */
	public AstNode getTarget() {
		return target;
	}

	/**
	 * Sets target object, and sets its parent to this node.
	 *
	 * @param target expression evaluating to the object upon which
	 *               to do the element lookup
	 * @throws IllegalArgumentException if target is {@code null}
	 */
	public void setTarget(AstNode target) {
		assertNotNull(target);
		this.target = target;
		target.setParent(this);
	}

	/**
	 * Returns the element being accessed
	 */
	public AstNode getElement() {
		return element;
	}

	/**
	 * Sets the element being accessed, and sets its parent to this node.
	 *
	 * @throws IllegalArgumentException if element is {@code null}
	 */
	public void setElement(AstNode element) {
		assertNotNull(element);
		this.element = element;
		element.setParent(this);
	}

	/**
	 * Returns left bracket position
	 */
	public int getLb() {
		return lb;
	}

	/**
	 * Sets left bracket position
	 */
	public void setLb(int lb) {
		this.lb = lb;
	}

	/**
	 * Returns right bracket position, -1 if missing
	 */
	public int getRb() {
		return rb;
	}

	/**
	 * Sets right bracket position, -1 if not present
	 */
	public void setRb(int rb) {
		this.rb = rb;
	}

	public void setParens(int lb, int rb) {
		this.lb = lb;
		this.rb = rb;
	}
}
