/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.skoow.rhino.ast;

import com.skoow.rhino.Node;
import com.skoow.rhino.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base type for {@link AstRoot} and {@link FunctionNode} nodes, which need to
 * collect much of the same information.
 */
public class ScriptNode extends Scope {

	private final List<FunctionNode> EMPTY_LIST = Collections.emptyList();
	private String sourceName;
	private int endLineno = -1;
	private List<FunctionNode> functions;
	private List<RegExpLiteral> regexps;
	private List<TemplateLiteral> templateLiterals;
	private List<AstSymbol> symbols = new ArrayList<>(4);
	private int paramCount = 0;
	private String[] variableNames;
	private boolean[] isConsts;

	private int tempNumber = 0;
	private boolean inStrictMode;

	{
		// during parsing, a ScriptNode or FunctionNode's top scope is itself
		this.top = this;
		this.type = Token.SCRIPT;
	}

	public ScriptNode() {
	}

	public ScriptNode(int pos) {
		super(pos);
	}

	/**
	 * Returns the URI, path or descriptive text indicating the origin
	 * of this script's source code.
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * Sets the URI, path or descriptive text indicating the origin
	 * of this script's source code.
	 */
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public int getBaseLineno() {
		return lineno;
	}

	/**
	 * Sets base (starting) line number for this script or function.
	 * This is a one-time operation, and throws an exception if the
	 * line number has already been set.
	 */
	public void setBaseLineno(int lineno) {
		if (lineno < 0 || this.lineno >= 0) {
			codeBug();
		}
		this.lineno = lineno;
	}

	public int getEndLineno() {
		return endLineno;
	}

	public void setEndLineno(int lineno) {
		// One time action
		if (lineno < 0 || endLineno >= 0) {
			codeBug();
		}
		endLineno = lineno;
	}

	public int getFunctionCount() {
		return functions == null ? 0 : functions.size();
	}

	public FunctionNode getFunctionNode(int i) {
		return functions.get(i);
	}

	public List<FunctionNode> getFunctions() {
		return functions == null ? EMPTY_LIST : functions;
	}

	/**
	 * Adds a {@link FunctionNode} to the functions table for codegen.
	 * Does not set the parent of the node.
	 *
	 * @return the index of the function within its parent
	 */
	public int addFunction(FunctionNode fnNode) {
		if (fnNode == null) {
			codeBug();
		}
		if (functions == null) {
			functions = new ArrayList<>();
		}
		functions.add(fnNode);
		return functions.size() - 1;
	}

	public int getRegexpCount() {
		return regexps == null ? 0 : regexps.size();
	}

	public String getRegexpString(int index) {
		return regexps.get(index).getValue();
	}

	public String getRegexpFlags(int index) {
		return regexps.get(index).getFlags();
	}

	/**
	 * Called by IRFactory to add a RegExp to the regexp table.
	 */
	public void addRegExp(RegExpLiteral re) {
		if (re == null) {
			codeBug();
		}
		if (regexps == null) {
			regexps = new ArrayList<>();
		}
		regexps.add(re);
		re.putIntProp(REGEXP_PROP, regexps.size() - 1);
	}

	public int getTemplateLiteralCount() {
		return templateLiterals == null ? 0 : templateLiterals.size();
	}

	public List<TemplateCharacters> getTemplateLiteralStrings(int index) {
		return templateLiterals.get(index).getTemplateStrings();
	}

	/**
	 * Called by IRFactory to add a Template Literal to the templateLiterals table.
	 */
	public void addTemplateLiteral(TemplateLiteral templateLiteral) {
		if (templateLiteral == null) {
			codeBug();
		}
		if (templateLiterals == null) {
			templateLiterals = new ArrayList<>();
		}
		templateLiterals.add(templateLiteral);
		templateLiteral.putIntProp(TEMPLATE_LITERAL_PROP, templateLiterals.size() - 1);
	}

	public int getIndexForNameNode(Node nameNode) {
		if (variableNames == null) {
			codeBug();
		}
		Scope node = nameNode.getScope();
		AstSymbol symbol = null;
		if (node != null && nameNode instanceof Name) {
			symbol = node.getSymbol(((Name) nameNode).getIdentifier());
		}
		return (symbol == null) ? -1 : symbol.getIndex();
	}

	public String getParamOrVarName(int index) {
		if (variableNames == null) {
			codeBug();
		}
		return variableNames[index];
	}

	public int getParamCount() {
		return paramCount;
	}

	public int getParamAndVarCount() {
		if (variableNames == null) {
			codeBug();
		}
		return symbols.size();
	}

	public String[] getParamAndVarNames() {
		if (variableNames == null) {
			codeBug();
		}
		return variableNames;
	}

	public boolean[] getParamAndVarConst() {
		if (variableNames == null) {
			codeBug();
		}
		return isConsts;
	}

	void addSymbol(AstSymbol symbol) {
		if (variableNames != null) {
			codeBug();
		}
		if (symbol.getDeclType() == Token.LP) {
			paramCount++;
		}
		symbols.add(symbol);
	}

	public List<AstSymbol> getSymbols() {
		return symbols;
	}

	public void setSymbols(List<AstSymbol> symbols) {
		this.symbols = symbols;
	}

	/**
	 * Assign every symbol a unique integer index. Generate arrays of variable
	 * names and constness that can be indexed by those indices.
	 *
	 * @param flattenAllTables if true, flatten all symbol tables,
	 *                         included nested block scope symbol tables. If false, just flatten the
	 *                         script's or function's symbol table.
	 */
	public void flattenSymbolTable(boolean flattenAllTables) {
		if (!flattenAllTables) {
			List<AstSymbol> newSymbols = new ArrayList<>();
			if (this.symbolTable != null) {
				// Just replace "symbols" with the symbols in this object's
				// symbol table. Can't just work from symbolTable map since
				// we need to retain duplicate parameters.
				for (int i = 0; i < symbols.size(); i++) {
					AstSymbol symbol = symbols.get(i);
					if (symbol.getContainingTable() == this) {
						newSymbols.add(symbol);
					}
				}
			}
			symbols = newSymbols;
		}
		variableNames = new String[symbols.size()];
		isConsts = new boolean[symbols.size()];
		for (int i = 0; i < symbols.size(); i++) {
			AstSymbol symbol = symbols.get(i);
			variableNames[i] = symbol.getName();
			isConsts[i] = symbol.getDeclType() == Token.CONST;
			symbol.setIndex(i);
		}
	}

	public String getNextTempName() {
		return "$" + tempNumber++;
	}

	public boolean isInStrictMode() {
		return inStrictMode;
	}

	public void setInStrictMode(boolean inStrictMode) {
		this.inStrictMode = inStrictMode;
	}
}
