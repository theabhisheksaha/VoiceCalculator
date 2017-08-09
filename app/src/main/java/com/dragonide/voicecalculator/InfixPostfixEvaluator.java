package com.dragonide.voicecalculator;

/**
 * Created by Ankit on 8/6/2017.
 */


import java.util.Stack;

/**
 * Class to evaluate expressions.
 *
 * @author Paul E. Davis (feedback@willcode4beer.com)
 */
public class InfixPostfixEvaluator {

    /**
     * Operators in reverse order of precedence.
     */
    private static final String operators = "-+/*";
    private static final String operands = "0123456789";

    private int getPrecedence(char operator) {
        int ret = 0;
        if (operator == '-' || operator == '+') {
            ret = 1;
        } else if (operator == '*' || operator == '/') {
            ret = 2;
        }
        return ret;
    }

    private boolean operatorGreaterOrEqual(char op1, char op2) {
        return getPrecedence(op1) >= getPrecedence(op2);
    }

    private boolean isOperator(char val) {
        return operators.indexOf(val) >= 0;
    }

    private boolean isOperand(char val) {
        return operands.indexOf(val) >= 0;
    }

    public String convert2Postfix(String infixExpr) {
        char[] chars = infixExpr.toCharArray();
        Stack<Character> stack = new Stack<Character>();
        StringBuilder out = new StringBuilder(infixExpr.length());

        for (char c : chars) {
            if (isOperator(c)) {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    if (operatorGreaterOrEqual(stack.peek(), c)) {
                        out.append(stack.pop());
                    } else {
                        break;
                    }
                }
                stack.push(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    out.append(stack.pop());
                }
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else if (isOperand(c)) {
                out.append(c);
            }
        }
        while (!stack.empty()) {
            out.append(stack.pop());
        }
        return out.toString();
    }

    public int evaluatePostfix(String postfixExpr) {
        char[] chars = postfixExpr.toCharArray();
        Stack<Integer> stack = new Stack<Integer>();
        for (char c : chars) {
            if (isOperand(c)) {
                stack.push(c - '0'); // convert char to int val
            } else if (isOperator(c)) {
                int op1 = stack.pop();
                int op2 = stack.pop();
                int result;
                switch (c) {
                    case '*':
                        result = op1 * op2;
                        stack.push(result);
                        break;
                    case '/':
                        result = op2 / op1;
                        stack.push(result);
                        break;
                    case '+':
                        result = op1 + op2;
                        stack.push(result);
                        break;
                    case '-':
                        result = op2 - op1;
                        stack.push(result);
                        break;
                }
            }
        }
        return stack.pop();
    }

    public int evalInfix(String infix) {
        return evaluatePostfix(convert2Postfix(infix));
    }


}
