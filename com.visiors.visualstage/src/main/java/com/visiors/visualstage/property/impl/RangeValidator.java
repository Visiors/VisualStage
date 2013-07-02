package com.visiors.visualstage.property.impl;

import java.util.HashMap;
import java.util.Map;

import com.visiors.visualstage.exception.AttributeException;

public class RangeValidator {

	private static String VARIABLE = "value";
	private static Map<String, TreeNode> pool = new HashMap<String, TreeNode>();

	public static boolean isValid(Object value, String expression) {

		TreeNode lookupTree = null;
		if (pool.containsKey(expression)) {
			lookupTree = pool.get(expression);
		} else {
			lookupTree = new TreeNode(expression);
			checkParentheses(expression);
			lookupTree.breakDown();
			pool.put(expression, lookupTree);
		}

		return lookupTree.isValid(value);
	}

	private static void checkParentheses(String exp){
		
		int parentheses = 0;
		char c;
		for (int i = 0; i < exp.length(); i++) {
			c = exp.charAt(i);
			if(c == '(')
				parentheses++;
			else if(c == ')')
				parentheses--;
		}
		if(parentheses != 0)
			throw new AttributeException("Parser error: Unbalanced parentheses in the expression: "+ exp);
		
	}
	
	
	private static class TreeNode {

		private static final String equalities = "<$<=$>$>=$==$!=";
		private static final String conditionals = "&&$||";

		TreeNode lc; // left child
		TreeNode rc; // right child
		String expression; // expression
		String equalityOperator; // equality operation
		String conditionalOperator; // equality operation

		public TreeNode(String expression) {
			this.expression = expression;
		}


		boolean isValid(Object value) {

			// leafs always return true
			if (lc == null && rc == null)
				return true;

			if (conditionalOperator != null) {
				boolean leftOperand = lc.isValid(value);
				boolean rightOperand = rc.isValid(value);
				if (conditionalOperator.equals("&&"))
					return leftOperand && rightOperand;
				if (conditionalOperator.equals("||"))
					return leftOperand || rightOperand;
			} else if (equalityOperator != null) {
				String strLeft = lc.expression;
				String strRight = rc.expression;
				String strNumber;
				if (strLeft.equalsIgnoreCase(VARIABLE))
					strNumber = strRight;
				else
					strNumber = strLeft;

				double d1 = Double.parseDouble(value.toString());
				double d2 = Double.parseDouble(strNumber);
				if (equalityOperator.equals(">"))
					return d1 > d2;
				if (equalityOperator.equals(">="))
					return d1 >= d2;
				if (equalityOperator.equals("<"))
					return d1 < d2;
				if (equalityOperator.equals("<="))
					return d1 <= d2;
				if (equalityOperator.equals("=="))
					return d1 == d2;
				if (equalityOperator.equals("!="))
					return d1 != d2;
			}

			return true;
		}

		void breakDown() {

			if (expression.length() == 0)
				return;

			StringBuffer leftExp = new StringBuffer();
			StringBuffer rightExp = new StringBuffer();
			StringBuffer rootExp = new StringBuffer();

			StringBuffer sb = new StringBuffer(expression);
			removeEnclosingParentheses(sb);
			expression = sb.toString();

			// look for && or ||
			splitAtConditional(leftExp, rootExp, rightExp);

			if (rootExp.length() > 0) {
				conditionalOperator = rootExp.toString();
			}
			// if exists no equality, look for operators <, <=, >, >=, == or !=
			else {
				rootExp.delete(0, rootExp.length());
				leftExp.delete(0, leftExp.length());
				rightExp.delete(0, rightExp.length());
				splitAtEquality(leftExp, rootExp, rightExp);
				if (rootExp.length() > 0)
					equalityOperator = rootExp.toString();
			}

			if (rootExp.length() > 0) {
				lc = new TreeNode(leftExp.toString());
				lc.breakDown();
				rc = new TreeNode(rightExp.toString());
				rc.breakDown();
			}

		}

		private void splitAtConditional(StringBuffer leftExp, StringBuffer operator,
				StringBuffer rightExp) {
			char c;
			StringBuffer sb = new StringBuffer(expression);
			int len = sb.length();
			int i = 0;
			String exp;
			int parentheses = 0;
			for (i = 0; i < len; i++) {

				c = sb.charAt(i);
				if (c == '(')
					++parentheses;
				else if (c == ')')
					--parentheses;
				else if (c == ' ')
					continue;
				else if (operator.length() == 0 && parentheses == 0
						&& conditionals.indexOf(c) != -1) {
					if (i == len - 1)
						throw new AttributeException("Parser error: incorrect expression: "
								+ sb.toString());

					exp = sb.substring(i, i+2);
					if (conditionals.indexOf(exp) != -1) {
						operator.append(exp);
						i++;
					} else {
						throw new AttributeException("Parser error: invalid operator '" + c + 
								"' in the expression '"+ expression + 
								"'. (Expected operators: '" + conditionals.replace("$", "' or '") + "')." );
					}
				} else {
					if (operator.length() == 0)
						leftExp.append(c);
					else
						rightExp.append(c);
				}
			}
		}

		private void splitAtEquality(StringBuffer leftExp, StringBuffer operator,
				StringBuffer rightExp) {
			char c;
			StringBuffer sb = new StringBuffer(expression);
			int len = sb.length();
			int i = 0;
			String exp;
			
			// look for equality operators
			int parentheses = 0;
			for (i = 0; i < len; i++) {

				c = sb.charAt(i);
				if (c == '(')
					++parentheses;
				else if (c == ')')
					--parentheses;
				else if (c == ' ')
					continue;
				else if (operator.length() == 0 && parentheses == 0 && equalities.indexOf(c) != -1) {
					if (i == len - 1)
						throw new AttributeException("Parser error: incorrect expression: "
								+ sb.toString());
					exp = sb.substring(i, i+2);
					if (equalities.indexOf(exp) != -1) {
						operator.append(exp);
						i++;
    				} else {
    					operator.append(c);
    					if(equalities.indexOf(exp.charAt(1)) != -1) {
							throw new AttributeException("Parser error: incorrect operatotr '"
									+ exp + "' in the expression '"+ expression +
									"'. (Expected operators: '" + equalities.replace("$", "' or '") + "')." );
    					}
    				}

				} else {
					if (operator.length() == 0)
						leftExp.append(c);
					else
						rightExp.append(c);
				}
			}
		}

		private void removeEnclosingParentheses(StringBuffer sb) {
			int len = sb.length();
			boolean parentheseLeft = sb.charAt(0) == '(';
			boolean parentheseRight = sb.charAt(len-1) == ')';
			if (parentheseLeft && parentheseRight) {
				sb.delete(len - 1, len);
				sb.delete(0, 1);
				removeEnclosingParentheses(sb);
			}
		}
	}

}
