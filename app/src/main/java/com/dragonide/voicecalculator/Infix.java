package com.dragonide.voicecalculator;


import java.util.Stack;
import java.util.StringTokenizer;


/**
 * Created by Ankit on 8/6/2017.
 */

public class Infix {

    public double infix(String expression)
    {
        //remove white space and add evaluation operator
        expression=expression.replaceAll("[\t\n ]", "")+"=";
        String operator="*/+-=";
        //split up the operators from the values
        StringTokenizer tokenizer=new StringTokenizer(expression, operator, true);
        Stack operatorStack=new Stack();
        Stack valueStack=new Stack();
        while(tokenizer.hasMoreTokens())
        {
            //add the next token to the proper stack
            String token=tokenizer.nextToken();
            if(operator.indexOf(token)<0)
                valueStack.push(token);
            else
                operatorStack.push(token);
            //perform any pending operations
            resolve(valueStack, operatorStack);
        }
        //return the top of the value stack
        String lastOne=(String)valueStack.pop();
        return Double.parseDouble(lastOne);
    }

    public int getPriority(String op)
    {
        if(op.equals("^"))
            return 1;
        else if(op.equals("*") || op.equals("/") || op.equals("%"))
            return 2;
        else if(op.equals("+") || op.equals("-"))
            return 3;
        else if(op.equals("="))
            return 4;
        else
            return Integer.MIN_VALUE;
    }
    public int factorial(int n){
        if (n == 0)
            return 1;
        else
            return(n * factorial(n-1));
    }
    public void resolve(Stack values,
                        Stack operators)
    {
        while(operators.size()>=2)
        {
            String first=(String)operators.pop();
            String second=(String)operators.pop();
            if(getPriority(first)<getPriority(second))
            {
                operators.push(second);
                operators.push(first);
                return;
            }
            else
            {
                String firstValue=(String)values.pop();
                String secondValue=(String)values.pop();
                values.push(getResults(secondValue, second, firstValue));
                operators.push(first);
            }
        }
    }

    public String getResults(String operand1, String operator, String operand2)
    {
        System.out.println("Performing "+
                operand1+operator+operand2);
        double op1=Double.parseDouble(operand1);
        double op2=Double.parseDouble(operand2);
        if(operator.equals("*"))
            return ""+(op1*op2);
        else if(operator.equals("/"))
            return ""+(op1/op2);
        else if(operator.equals("+"))
            return ""+(op1+op2);
        else if(operator.equals("-"))
            return ""+(op1-op2);
        else if(operator.equals("^"))
            return ""+ (Math.pow(op1, op2));
        else if(operator.equals("%"))
            return  "" + ((op1/op2)-Math.floor(op1/op2))*op2;
        else
            return null;
    }



}
