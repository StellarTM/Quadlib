/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.skoow.rhino;

import com.skoow.rhino.ast.Comment;
import com.skoow.rhino.ast.Jump;
import com.skoow.rhino.ast.Name;
import com.skoow.rhino.ast.NumberLiteral;
import com.skoow.rhino.ast.Scope;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class implements the root of the intermediate representation.
 *
 * @author Norris Boyd
 * @author Mike McCabe
 */
public class Node implements Iterable<Node> {
	public static final int FUNCTION_PROP = 1;
	public static final int LOCAL_PROP = 2;
	public static final int LOCAL_BLOCK_PROP = 3;
	public static final int REGEXP_PROP = 4;
	public static final int CASEARRAY_PROP = 5;

	//  the following properties are defined and manipulated by the
	//  optimizer -
	//  TARGETBLOCK_PROP - the block referenced by a branch node
	//  VARIABLE_PROP - the variable referenced by a BIND or NAME node
	//  ISNUMBER_PROP - this node generates code on Number children and
	//                  delivers a Number result (as opposed to Objects)
	//  DIRECTCALL_PROP - this call node should emit code to test the function
	//                    object against the known class and call direct if it
	//                    matches.

	public static final int TARGETBLOCK_PROP = 6;
	public static final int VARIABLE_PROP = 7;
	public static final int ISNUMBER_PROP = 8;
	public static final int DIRECTCALL_PROP = 9;
	public static final int SPECIALCALL_PROP = 10;
	public static final int SKIP_INDEXES_PROP = 11; // array of skipped indexes of array literal
	public static final int OBJECT_IDS_PROP = 12; // array of properties for object literal
	public static final int INCRDECR_PROP = 13; // pre or post type of increment/decrement
	public static final int CATCH_SCOPE_PROP = 14; // index of catch scope block in catch
	public static final int LABEL_ID_PROP = 15; // label id: code generation uses it
	public static final int MEMBER_TYPE_PROP = 16; // type of element access operation
	public static final int NAME_PROP = 17; // property name
	public static final int CONTROL_BLOCK_PROP = 18; // flags a control block that can drop off
	public static final int PARENTHESIZED_PROP = 19; // expression is parenthesized
	public static final int GENERATOR_END_PROP = 20;
	public static final int DESTRUCTURING_ARRAY_LENGTH = 21;
	public static final int DESTRUCTURING_NAMES = 22;
	public static final int DESTRUCTURING_PARAMS = 23;
	public static final int JSDOC_PROP = 24;
	public static final int EXPRESSION_CLOSURE_PROP = 25; // JS 1.8 expression closure pseudo-return
	public static final int DESTRUCTURING_SHORTHAND = 26; // JS 1.8 destructuring shorthand
	public static final int ARROW_FUNCTION_PROP = 27;
	public static final int TEMPLATE_LITERAL_PROP = 28;
	public static final int LAST_PROP = 28;

	// values of ISNUMBER_PROP to specify
	// which of the children are Number types
	public static final int BOTH = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;

	public static final int    // values for SPECIALCALL_PROP
			NON_SPECIALCALL = 0;
	public static final int SPECIALCALL_EVAL = 1;
	public static final int SPECIALCALL_WITH = 2;

	public static final int   // flags for INCRDECR_PROP
			DECR_FLAG = 0x1;
	public static final int POST_FLAG = 0x2;

	public static final int   // flags for MEMBER_TYPE_PROP
			PROPERTY_FLAG = 0x1; // property access: element is valid name
	public static final int ATTRIBUTE_FLAG = 0x2; // x.@y or x..@y
	public static final int DESCENDANTS_FLAG = 0x4; // x..y or x..@i
	/**
	 * These flags enumerate the possible ways a statement/function can
	 * terminate. These flags are used by endCheck() and by the Parser to
	 * detect inconsistent return usage.
	 * <p>
	 * END_UNREACHED is reserved for code paths that are assumed to always be
	 * able to execute (example: throw, continue)
	 * <p>
	 * END_DROPS_OFF indicates if the statement can transfer control to the
	 * next one. Statement such as return dont. A compound statement may have
	 * some branch that drops off control to the next statement.
	 * <p>
	 * END_RETURNS indicates that the statement can return (without arguments)
	 * END_RETURNS_VALUE indicates that the statement can return a value.
	 * <p>
	 * A compound statement such as
	 * if (condition) {
	 * return value;
	 * }
	 * Will be detected as (END_DROPS_OFF | END_RETURN_VALUE) by endCheck()
	 */
	public static final int END_UNREACHED = 0;
	public static final int END_DROPS_OFF = 1;
	public static final int END_RETURNS = 2;
	public static final int END_RETURNS_VALUE = 4;
	public static final int END_YIELDS = 8;
	private static final Node NOT_SET = new Node(Token.ERROR);

	private static class PropListItem {
		PropListItem next;
		int type;
		int intValue;
		Object objectValue;
	}

	public static Node newNumber(double number) {
		NumberLiteral n = new NumberLiteral();
		n.setNumber(number);
		return n;
	}

	public static Node newString(String str) {
		return newString(Token.STRING, str);
	}

	public static Node newString(int type, String str) {
		Name name = new Name();
		name.setIdentifier(str);
		name.setType(type);
		return name;
	}

	public static Node newTarget() {
		return new Node(Token.TARGET);
	}

	protected int type = Token.ERROR; // type of the node, e.g. Token.NAME
	protected Node next;             // next sibling
	protected Node first;    // first element of a linked list of children
	protected Node last;     // last element of a linked list of children
	protected int lineno = -1;
	/**
	 * Linked list of properties. Since vast majority of nodes would have
	 * no more then 2 properties, linked list saves memory and provides
	 * fast lookup. If this does not holds, propListHead can be replaced
	 * by UintMap.
	 */
	protected PropListItem propListHead;

	public Node(int nodeType) {
		type = nodeType;
	}

	public Node(int nodeType, Node child) {
		type = nodeType;
		first = last = child;
		child.next = null;
	}

	public Node(int nodeType, Node left, Node right) {
		type = nodeType;
		first = left;
		last = right;
		left.next = right;
		right.next = null;
	}

	public Node(int nodeType, Node left, Node mid, Node right) {
		type = nodeType;
		first = left;
		last = right;
		left.next = mid;
		mid.next = right;
		right.next = null;
	}

	public Node(int nodeType, int line) {
		type = nodeType;
		lineno = line;
	}

	public Node(int nodeType, Node child, int line) {
		this(nodeType, child);
		lineno = line;
	}

	public Node(int nodeType, Node left, Node right, int line) {
		this(nodeType, left, right);
		lineno = line;
	}

	public Node(int nodeType, Node left, Node mid, Node right, int line) {
		this(nodeType, left, mid, right);
		lineno = line;
	}

	public int getType() {
		return type;
	}

	/**
	 * Sets the node type and returns this node.
	 */
	public Node setType(int type) {
		this.type = type;
		return this;
	}

	/**
	 * Gets the JsDoc comment string attached to this node.
	 *
	 * @return the comment string or {@code null} if no JsDoc is attached to
	 * this node
	 */
	public String getJsDoc() {
		Comment comment = getJsDocNode();
		if (comment != null) {
			return comment.getValue();
		}
		return null;
	}

	/**
	 * Gets the JsDoc Comment object attached to this node.
	 *
	 * @return the Comment or {@code null} if no JsDoc is attached to
	 * this node
	 */
	public Comment getJsDocNode() {
		return (Comment) getProp(JSDOC_PROP);
	}

	/**
	 * Sets the JsDoc comment string attached to this node.
	 */
	public void setJsDocNode(Comment jsdocNode) {
		putProp(JSDOC_PROP, jsdocNode);
	}

	public boolean hasChildren() {
		return first != null;
	}

	public Node getFirstChild() {
		return first;
	}

	public Node getLastChild() {
		return last;
	}

	public Node getNext() {
		return next;
	}

	public Node getChildBefore(Node child) {
		if (child == first) {
			return null;
		}
		Node n = first;
		while (n.next != child) {
			n = n.next;
			if (n == null) {
				throw new RuntimeException("node is not a child");
			}
		}
		return n;
	}

	public Node getLastSibling() {
		Node n = this;
		while (n.next != null) {
			n = n.next;
		}
		return n;
	}

	public void addChildToFront(Node child) {
		child.next = first;
		first = child;
		if (last == null) {
			last = child;
		}
	}

	public void addChildToBack(Node child) {
		child.next = null;
		if (last == null) {
			first = last = child;
			return;
		}
		last.next = child;
		last = child;
	}

	public void addChildrenToFront(Node children) {
		Node lastSib = children.getLastSibling();
		lastSib.next = first;
		first = children;
		if (last == null) {
			last = lastSib;
		}
	}

	public void addChildrenToBack(Node children) {
		if (last != null) {
			last.next = children;
		}
		last = children.getLastSibling();
		if (first == null) {
			first = children;
		}
	}

	/**
	 * Add 'child' before 'node'.
	 */
	public void addChildBefore(Node newChild, Node node) {
		if (newChild.next != null) {
			throw new RuntimeException("newChild had siblings in addChildBefore");
		}
		if (first == node) {
			newChild.next = first;
			first = newChild;
			return;
		}
		Node prev = getChildBefore(node);
		addChildAfter(newChild, prev);
	}

	/**
	 * Add 'child' after 'node'.
	 */
	public void addChildAfter(Node newChild, Node node) {
		if (newChild.next != null) {
			throw new RuntimeException("newChild had siblings in addChildAfter");
		}
		newChild.next = node.next;
		node.next = newChild;
		if (last == node) {
			last = newChild;
		}
	}

	public void removeChild(Node child) {
		Node prev = getChildBefore(child);
		if (prev == null) {
			first = first.next;
		} else {
			prev.next = child.next;
		}
		if (child == last) {
			last = prev;
		}
		child.next = null;
	}

	public void replaceChild(Node child, Node newChild) {
		newChild.next = child.next;
		if (child == first) {
			first = newChild;
		} else {
			Node prev = getChildBefore(child);
			prev.next = newChild;
		}
		if (child == last) {
			last = newChild;
		}
		child.next = null;
	}

	public void replaceChildAfter(Node prevChild, Node newChild) {
		Node child = prevChild.next;
		newChild.next = child.next;
		prevChild.next = newChild;
		if (child == last) {
			last = newChild;
		}
		child.next = null;
	}

	public void removeChildren() {
		first = last = null;
	}

	/**
	 * Returns an {@link Iterator} over the node's children.
	 */
	@Override
	public Iterator<Node> iterator() {
		return new NodeIterator();
	}

	private PropListItem lookupProperty(int propType) {
		PropListItem x = propListHead;
		while (x != null && propType != x.type) {
			x = x.next;
		}
		return x;
	}

	private PropListItem ensureProperty(int propType) {
		PropListItem item = lookupProperty(propType);
		if (item == null) {
			item = new PropListItem();
			item.type = propType;
			item.next = propListHead;
			propListHead = item;
		}
		return item;
	}

	public void removeProp(int propType) {
		PropListItem x = propListHead;
		if (x != null) {
			PropListItem prev = null;
			while (x.type != propType) {
				prev = x;
				x = x.next;
				if (x == null) {
					return;
				}
			}
			if (prev == null) {
				propListHead = x.next;
			} else {
				prev.next = x.next;
			}
		}
	}

	public Object getProp(int propType) {
		PropListItem item = lookupProperty(propType);
		if (item == null) {
			return null;
		}
		return item.objectValue;
	}

	public int getIntProp(int propType, int defaultValue) {
		PropListItem item = lookupProperty(propType);
		if (item == null) {
			return defaultValue;
		}
		return item.intValue;
	}

	public int getExistingIntProp(int propType) {
		PropListItem item = lookupProperty(propType);
		if (item == null) {
			Kit.codeBug();
		}
		return item.intValue;
	}

	public void putProp(int propType, Object prop) {
		if (prop == null) {
			removeProp(propType);
		} else {
			PropListItem item = ensureProperty(propType);
			item.objectValue = prop;
		}
	}

	public void putIntProp(int propType, int prop) {
		PropListItem item = ensureProperty(propType);
		item.intValue = prop;
	}


	/**
	 * Does consistent-return analysis on the function body when strict mode is
	 * enabled.
	 *
	 *   function (x) { return (x+1) }
	 * is ok, but
	 *   function (x) { if (x &lt; 0) return (x+1); }
	 * is not becuase the function can potentially return a value when the
	 * condition is satisfied and if not, the function does not explicitly
	 * return value.
	 *
	 * This extends to checking mismatches such as "return" and "return <value>"
	 * used in the same function. Warnings are not emitted if inconsistent
	 * returns exist in code that can be statically shown to be unreachable.
	 * Ex.
	 * <pre>function (x) { while (true) { ... if (..) { return value } ... } }
	 * </pre>
	 * emits no warning. However if the loop had a break statement, then a
	 * warning would be emitted.
	 *
	 * The consistency analysis looks at control structures such as loops, ifs,
	 * switch, try-catch-finally blocks, examines the reachable code paths and
	 * warns the user about an inconsistent set of termination possibilities.
	 *
	 * Caveat: Since the parser flattens many control structures into almost
	 * straight-line code with gotos, it makes such analysis hard. Hence this
	 * analyser is written to taken advantage of patterns of code generated by
	 * the parser (for loops, try blocks and such) and does not do a full
	 * control flow analysis of the gotos and break/continue statements.
	 * Future changes to the parser will affect this analysis.
	 */

	/**
	 * Return the line number recorded for this node.
	 *
	 * @return the line number
	 */
	public int getLineno() {
		return lineno;
	}

	public void setLineno(int lineno) {
		this.lineno = lineno;
	}

	/**
	 * Can only be called when <code>getType() == Token.NUMBER</code>
	 */
	public final double getDouble() {
		return ((NumberLiteral) this).getNumber();
	}

	public final void setDouble(double number) {
		((NumberLiteral) this).setNumber(number);
	}

	/**
	 * Can only be called when node has String context.
	 */
	public final String getString() {
		return ((Name) this).getIdentifier();
	}

	/**
	 * Can only be called when node has String context.
	 */
	public final void setString(String s) {
		if (s == null) {
			Kit.codeBug();
		}
		((Name) this).setIdentifier(s);
	}

	/**
	 * Can only be called when node has String context.
	 */
	public Scope getScope() {
		return this.getScope();
	}

	/**
	 * Can only be called when node has String context.
	 */
	public void setScope(Scope s) {
		throw Kit.codeBug();
	}

	public final int labelId() {
		if ((type != Token.TARGET) && (type != Token.YIELD) && (type != Token.YIELD_STAR)) {
			Kit.codeBug();
		}
		return getIntProp(LABEL_ID_PROP, -1);
	}

	public void labelId(int labelId) {
		if ((type != Token.TARGET) && (type != Token.YIELD) && (type != Token.YIELD_STAR)) {
			Kit.codeBug();
		}
		putIntProp(LABEL_ID_PROP, labelId);
	}

	/**
	 * Checks that every return usage in a function body is consistent with the
	 * requirements of strict-mode.
	 *
	 * @return true if the function satisfies strict mode requirement.
	 */
	public boolean hasConsistentReturnUsage() {
		int n = endCheck();
		return (n & END_RETURNS_VALUE) == 0 || (n & (END_DROPS_OFF | END_RETURNS | END_YIELDS)) == 0;
	}

	/**
	 * Returns in the then and else blocks must be consistent with each other.
	 * If there is no else block, then the return statement can fall through.
	 *
	 * @return logical OR of END_* flags
	 */
	private int endCheckIf() {
		Node th, el;
		int rv = END_UNREACHED;

		th = next;
		el = ((Jump) this).target;

		rv = th.endCheck();

		if (el != null) {
			rv |= el.endCheck();
		} else {
			rv |= END_DROPS_OFF;
		}

		return rv;
	}

	/**
	 * Consistency of return statements is checked between the case statements.
	 * If there is no default, then the switch can fall through. If there is a
	 * default,we check to see if all code paths in the default return or if
	 * there is a code path that can fall through.
	 *
	 * @return logical OR of END_* flags
	 */
	private int endCheckSwitch() {
		int rv = END_UNREACHED;

		// examine the cases
		//         for (n = first.next; n != null; n = n.next)
		//         {
		//             if (n.type == Token.CASE) {
		//                 rv |= ((Jump)n).target.endCheck();
		//             } else
		//                 break;
		//         }

		//         // we don't care how the cases drop into each other
		//         rv &= ~END_DROPS_OFF;

		//         // examine the default
		//         n = ((Jump)this).getDefault();
		//         if (n != null)
		//             rv |= n.endCheck();
		//         else
		//             rv |= END_DROPS_OFF;

		//         // remove the switch block
		//         rv |= getIntProp(CONTROL_BLOCK_PROP, END_UNREACHED);

		return rv;
	}

	/**
	 * If the block has a finally, return consistency is checked in the
	 * finally block. If all code paths in the finally returns, then the
	 * returns in the try-catch blocks don't matter. If there is a code path
	 * that does not return or if there is no finally block, the returns
	 * of the try and catch blocks are checked for mismatch.
	 *
	 * @return logical OR of END_* flags
	 */
	private int endCheckTry() {
		int rv = END_UNREACHED;

		// a TryStatement isn't a jump - needs rewriting

		// check the finally if it exists
		//         n = ((Jump)this).getFinally();
		//         if(n != null) {
		//             rv = n.next.first.endCheck();
		//         } else {
		//             rv = END_DROPS_OFF;
		//         }

		//         // if the finally block always returns, then none of the returns
		//         // in the try or catch blocks matter
		//         if ((rv & END_DROPS_OFF) != 0) {
		//             rv &= ~END_DROPS_OFF;

		//             // examine the try block
		//             rv |= first.endCheck();

		//             // check each catch block
		//             n = ((Jump)this).target;
		//             if (n != null)
		//             {
		//                 // point to the first catch_scope
		//                 for (n = n.next.first; n != null; n = n.next.next)
		//                 {
		//                     // check the block of user code in the catch_scope
		//                     rv |= n.next.first.next.first.endCheck();
		//                 }
		//             }
		//         }

		return rv;
	}

	/**
	 * Return statement in the loop body must be consistent. The default
	 * assumption for any kind of a loop is that it will eventually terminate.
	 * The only exception is a loop with a constant true condition. Code that
	 * follows such a loop is examined only if one can statically determine
	 * that there is a break out of the loop.
	 * <pre>
	 *  for(&lt;&gt; ; &lt;&gt;; &lt;&gt;) {}
	 *  for(&lt;&gt; in &lt;&gt; ) {}
	 *  while(&lt;&gt;) { }
	 *  do { } while(&lt;&gt;)
	 * </pre>
	 *
	 * @return logical OR of END_* flags
	 */
	private int endCheckLoop() {
		Node n;
		int rv = END_UNREACHED;

		// To find the loop body, we look at the second to last node of the
		// loop node, which should be the predicate that the loop should
		// satisfy.
		// The target of the predicate is the loop-body for all 4 kinds of
		// loops.
		for (n = first; n.next != last; n = n.next) {
			/* skip */
		}
		if (n.type != Token.IFEQ) {
			return END_DROPS_OFF;
		}

		// The target's next is the loop body block
		rv = ((Jump) n).target.next.endCheck();

		// check to see if the loop condition is true
		if (n.first.type == Token.TRUE) {
			rv &= ~END_DROPS_OFF;
		}

		// look for effect of breaks
		rv |= getIntProp(CONTROL_BLOCK_PROP, END_UNREACHED);

		return rv;
	}

	/**
	 * A general block of code is examined statement by statement. If any
	 * statement (even compound ones) returns in all branches, then subsequent
	 * statements are not examined.
	 *
	 * @return logical OR of END_* flags
	 */
	private int endCheckBlock() {
		Node n;
		int rv = END_DROPS_OFF;

		// check each statment and if the statement can continue onto the next
		// one, then check the next statement
		for (n = first; ((rv & END_DROPS_OFF) != 0) && n != null; n = n.next) {
			rv &= ~END_DROPS_OFF;
			rv |= n.endCheck();
		}
		return rv;
	}

	/**
	 * A labelled statement implies that there maybe a break to the label. The
	 * function processes the labelled statement and then checks the
	 * CONTROL_BLOCK_PROP property to see if there is ever a break to the
	 * particular label.
	 *
	 * @return logical OR of END_* flags
	 */
	private int endCheckLabel() {
		int rv = END_UNREACHED;

		rv = next.endCheck();
		rv |= getIntProp(CONTROL_BLOCK_PROP, END_UNREACHED);

		return rv;
	}

	/**
	 * When a break is encountered annotate the statement being broken
	 * out of by setting its CONTROL_BLOCK_PROP property.
	 *
	 * @return logical OR of END_* flags
	 */
	private int endCheckBreak() {
		Node n = ((Jump) this).getJumpStatement();
		n.putIntProp(CONTROL_BLOCK_PROP, END_DROPS_OFF);
		return END_UNREACHED;
	}

	/**
	 * endCheck() examines the body of a function, doing a basic reachability
	 * analysis and returns a combination of flags END_* flags that indicate
	 * how the function execution can terminate. These constitute only the
	 * pessimistic set of termination conditions. It is possible that at
	 * runtime certain code paths will never be actually taken. Hence this
	 * analysis will flag errors in cases where there may not be errors.
	 *
	 * @return logical OR of END_* flags
	 */
	private int endCheck() {
		switch (type) {
			case Token.BREAK:
				return endCheckBreak();

			case Token.EXPR_VOID:
				if (this.first != null) {
					return first.endCheck();
				}
				return END_DROPS_OFF;

			case Token.YIELD:
			case Token.YIELD_STAR:
				return END_YIELDS;

			case Token.CONTINUE:
			case Token.THROW:
				return END_UNREACHED;

			case Token.RETURN:
				if (this.first != null) {
					return END_RETURNS_VALUE;
				}
				return END_RETURNS;

			case Token.TARGET:
				if (next != null) {
					return next.endCheck();
				}
				return END_DROPS_OFF;

			case Token.LOOP:
				return endCheckLoop();

			case Token.LOCAL_BLOCK:
			case Token.BLOCK:
				// there are several special kinds of blocks
				if (first == null) {
					return END_DROPS_OFF;
				}

				return switch (first.type) {
					case Token.LABEL -> first.endCheckLabel();
					case Token.IFNE -> first.endCheckIf();
					case Token.SWITCH -> first.endCheckSwitch();
					case Token.TRY -> first.endCheckTry();
					default -> endCheckBlock();
				};

			default:
				return END_DROPS_OFF;
		}
	}

	public boolean hasSideEffects() {
		switch (type) {
			case Token.EXPR_VOID:
			case Token.COMMA:
				if (last != null) {
					return last.hasSideEffects();
				}
				return true;

			case Token.HOOK:
				if (first == null || first.next == null || first.next.next == null) {
					Kit.codeBug();
				}
				return first.next.hasSideEffects() && first.next.next.hasSideEffects();

			case Token.AND:
			case Token.OR:
			case Token.NULLISH_COALESCING:
				if (first == null || last == null) {
					Kit.codeBug();
				}
				return first.hasSideEffects() || last.hasSideEffects();

			case Token.ERROR:         // Avoid cascaded error messages
			case Token.EXPR_RESULT:
			case Token.ASSIGN:
			case Token.ASSIGN_ADD:
			case Token.ASSIGN_SUB:
			case Token.ASSIGN_MUL:
			case Token.ASSIGN_DIV:
			case Token.ASSIGN_MOD:
			case Token.ASSIGN_BITOR:
			case Token.ASSIGN_BITXOR:
			case Token.ASSIGN_BITAND:
			case Token.ASSIGN_LSH:
			case Token.ASSIGN_RSH:
			case Token.ASSIGN_URSH:
			case Token.ENTERWITH:
			case Token.LEAVEWITH:
			case Token.RETURN:
			case Token.GOTO:
			case Token.IFEQ:
			case Token.IFNE:
			case Token.NEW:
			case Token.DELPROP:
			case Token.SETNAME:
			case Token.SETPROP:
			case Token.SETELEM:
			case Token.CALL:
			case Token.THROW:
			case Token.RETHROW:
			case Token.SETVAR:
			case Token.CATCH_SCOPE:
			case Token.RETURN_RESULT:
			case Token.SET_REF:
			case Token.DEL_REF:
			case Token.REF_CALL:
			case Token.TRY:
			case Token.SEMI:
			case Token.INC:
			case Token.DEC:
			case Token.IF:
			case Token.ELSE:
			case Token.SWITCH:
			case Token.WHILE:
			case Token.DO:
			case Token.FOR:
			case Token.BREAK:
			case Token.CONTINUE:
			case Token.VAR:
			case Token.CONST:
			case Token.LET:
			case Token.LETEXPR:
			case Token.WITH:
			case Token.WITHEXPR:
			case Token.CATCH:
			case Token.FINALLY:
			case Token.BLOCK:
			case Token.LABEL:
			case Token.TARGET:
			case Token.LOOP:
			case Token.JSR:
			case Token.SETPROP_OP:
			case Token.SETELEM_OP:
			case Token.LOCAL_BLOCK:
			case Token.SET_REF_OP:
			case Token.YIELD:
			case Token.YIELD_STAR:
				return true;

			default:
				return false;
		}
	}

	/**
	 * Recursively unlabel every TARGET or YIELD node in the tree.
	 * <p>
	 * This is used and should only be used for inlining finally blocks where
	 * jsr instructions used to be. It is somewhat hackish, but implementing
	 * a clone() operation would take much, much more effort.
	 * <p>
	 * This solution works for inlining finally blocks because you should never
	 * be writing any given block to the class file simultaneously. Therefore,
	 * an unlabeling will never occur in the middle of a block.
	 */
	public void resetTargets() {
		if (type == Token.FINALLY) {
			resetTargets_r();
		} else {
			Kit.codeBug();
		}
	}

	private void resetTargets_r() {
		if (type == Token.TARGET || type == Token.YIELD || type == Token.YIELD_STAR) {
			labelId(-1);
		}
		Node child = first;
		while (child != null) {
			child.resetTargets_r();
			child = child.next;
		}
	}

	@Override
	public String toString() {
		return String.valueOf(type);
	}

	/**
	 * Iterates over the children of this Node.  Supports child removal.  Not
	 * thread-safe.  If anyone changes the child list before the iterator
	 * finishes, the results are undefined and probably bad.
	 */
	public class NodeIterator implements Iterator<Node> {
		private Node cursor;  // points to node to be returned next
		private Node prev = NOT_SET;
		private Node prev2;
		private boolean removed = false;

		public NodeIterator() {
			cursor = Node.this.first;
		}

		@Override
		public boolean hasNext() {
			return cursor != null;
		}

		@Override
		public Node next() {
			if (cursor == null) {
				throw new NoSuchElementException();
			}
			removed = false;
			prev2 = prev;
			prev = cursor;
			cursor = cursor.next;
			return prev;
		}

		@Override
		public void remove() {
			if (prev == NOT_SET) {
				throw new IllegalStateException("next() has not been called");
			}
			if (removed) {
				throw new IllegalStateException("remove() already called for current element");
			}
			if (prev == first) {
				first = prev.next;
			} else if (prev == last) {
				prev2.next = null;
				last = prev2;
			} else {
				prev2.next = cursor;
			}
		}
	}
}
