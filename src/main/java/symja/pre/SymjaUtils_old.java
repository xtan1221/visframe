/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symja.pre;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
/**
 *
 * @author tanxu
 */
public class SymjaUtils_old {
    
    protected final static ExprEvaluator SYMJA_EVALUATOR = new ExprEvaluator();
    /**
     * check if the given string is the string name of a built in symbol;
     * 
     * @param expression
     * @return 
     */
    public static boolean isBuiltInSymbol(String expression){
        if(!isValidSymjaExpressionString(expression)){
            return false;
        }
        
        IExpr v = SYMJA_EVALUATOR.eval(expression);
        return v.isBuiltInSymbol();
        
//        return true;
    }
    
    
    /**
     * 
     * @param expression
     * @return 
     */
    private static boolean isAtomInSymja(String expression){
        if(!isValidSymjaExpressionString(expression)){
            return false;
        }
        
        IExpr v = SYMJA_EVALUATOR.eval(expression);
        return v.isAtom();
    }
    
    /**
     * check if the given string is a valid symja variable string;
     * only characters and/or numbers are allowed in a variable name;
     * @param expression
     * @return 
     */
    private static boolean isVariable(String expression){
        if(!isValidSymjaExpressionString(expression)){
            return false;
        }
        IExpr v = SYMJA_EVALUATOR.eval(expression);
        return v.isVariable();
    }
    
    /**
     * extract all the atomic, non-built-in-symbol string components that are located at the leaf nodes on the symja string structure;
     * note that if a numeric string is used as a parameter of a built in function, it will also be extracted;
     * @param expressionString
     * @return 
     */
    private static Set<String> extractAtomNonBuiltInStringComponentSet(String expressionString){
        System.out.println(expressionString);
        Set<String> ret = new HashSet<>();
        //{v1, v2, .....}
        if(!isValidSymjaExpressionString(expressionString)){//
            return null;
        }
        
//        IExpr result = SYMJA_EVALUATOR.eval("Variables("+expressionString+")");
        IExpr result = SYMJA_EVALUATOR.eval(expressionString);
        System.out.println(result);
//        System.out.println(result.size());//number of children AST
//        System.out.println(result.leafCount()); //number of leaf AST
        
        for(int i=0;i<result.size();i++){ //the first element with index 0 is always list, thus skip it
            IExpr v = result.getAt(i);
//            System.out.println(i+":"+v
//                        +";size="+v.size()
//                        +";leafCount="+v.leafCount()
//                        +";isAtom="+v.isAtom()
            
            
            if(v.isAtom()){ //the detected variable AST is atom and has no children AST, thus a real variable
                if(!v.isBuiltInSymbol()){
                    ret.add(v.toString());
                }
            }else{//the detected variable can be further decomposed, but not be parseable by VARIABLES function, thus need to break it down alternatively here
                System.out.println(v+"========================"+v.size());
                for(int j = 0;j<v.size();j++){
                    IExpr vv = v.getAt(j);
                    System.out.println(j+":"+vv
                        +";size="+vv.size()
                        +";leafCount="+vv.leafCount()
                        +";isAtom="+vv.isAtom()
                        +";isVariable="+vv.isVariable()
                        +";isBuiltInSymbol="+vv.isBuiltInSymbol()
                    );
                    if(vv.isAtom()){
                        if(!vv.isBuiltInSymbol()){
                            
                           ret.add(vv.toString());
                            
                        }
                    }else{//further 
                        ret.addAll(extractAtomNonBuiltInStringComponentSet(vv.toString()));
                    }
                }
            }
        }
        
        
        
        return ret;
    }
    
    
    private static boolean isNumeric(String inputString){
        try{
            double n = Double.parseDouble(inputString);
        }catch(Exception e){
            return false;
        }
        
        return true;
        
    }
    
    
    
    public void extractFunctionAndVariableType(String expression){
        
    }
    
    public static boolean isNumericExpression(String expression){
        return true;
    }
    
    
    public static boolean isLogicalExpression(String expression){
        return true;
    }
    
    
    
    
    
    /**
     * check if the given variable is an atom non-built in symja symbol;
     * if yes, the input string can be used as a user-defined variable name for symja engine;
     * note that for symbol name string, the leading and tailing empty spaces should be removed;
     * for example:
     * "     sd   ", "sd     ", "    sd" will all be recognized as the same "sd"
     * "   s    d" is not valid
     * 
     * @param symbolString
     * @return 
     */
    public static boolean isValidUserDefinedSymjaVariableName(String symbolString){
   
        return isAtomInSymja(symbolString)&&!isBuiltInSymbol(symbolString)&&isVariable(symbolString);
        
    }
    
    
    
    /**
     * check if the given expression string is a valid symja format string
     * @param expression
     * @return 
     */
    public static boolean isValidSymjaExpressionString(String expression){
        try{
            IExpr result = SYMJA_EVALUATOR.eval(expression);
//            result.isVariable();
            System.out.println(
                        "size="+result.size()
                        +";leafCount="+result.leafCount()
                        +";isAtom="+result.isAtom()
                        
                        +";isVariable="+result.isVariable()
                        +";isBuiltInSymbol="+result.isBuiltInSymbol());
            
            for(int i=0;i<result.size();i++){
                IExpr child = result.getAt(i);
                System.out.println(child);
                if(child.isAtom()){
                    System.out.println("============is atom"); 
                    System.out.println("==isValue:"+child.isValue());
                    System.out.println("==isConstantAttribute:"+child.isConstantAttribute());
                    System.out.println("==isNumeric:"+child.isNumeric());
                    System.out.println("==isNumber:"+child.isNumber());
                    System.out.println("==isString:"+child.isString());
                    
                    System.out.println("==isBuiltInSymbol:"+child.isBuiltInSymbol());
                    System.out.println("==isVariable:"+child.isVariable());
                    
                    System.out.println("isAST:"+child.isAST());
                    System.out.println("isPlus:"+child.isPlus());
                    
                }else{
                    System.out.println("==============is not atom");
                    isValidSymjaExpressionString(child.toString());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    /**
     * 1. every user defined variable names must be valid
     * 2. the expression contains a valid math expression
     * @param expression
     * @return 
     */
    public static boolean isValidClosedFormMathSymjaExpressionString(String expression){
        if(!isValidSymjaExpressionString(expression)){
            return false;
        }
        
        
        
        
        return true;
    }
    
    
    /**
     * extract and return a set of all non built in valid symja variable names in the given valid symja expression string;
     * return null if the given expression string is not valid symja expression; 
     * note that the given expression is not necessarily a closed form math expression;!!!!!!!!!
     * @param expressionString
     * @return 
     */
    public static Set<String> extractNonBuiltInVariableNameSet(String expressionString){
        if(!isValidSymjaExpressionString(expressionString)){
            return null;
        }
        
        Set<String> ret = new HashSet<>();
        
        Set<String> atomNonBuiltInStringComponentSet = extractAtomNonBuiltInStringComponentSet(expressionString);
        
        for(String s:atomNonBuiltInStringComponentSet){
            if(isVariable(s)){
                ret.add(s);
            }else{
                if(!isNumeric(s)){
                    return null;
                }
            }
        }
        
        
        return ret;
    }
    
    
    
    
    /**
     * check if the expression string is in valid symja format and contains only the given set of variables
     * @param expressionString
     * @param variableNameSet 
     * @return  
     */
    public static boolean validateExpressionString(String expressionString, Set<String> variableNameSet){
        return true;
    }
    
    
    /**
     * return the symja solution as a list of strings of the input expression
     * @param expression
     * @param knownVariableValueMap
     * @return 
     */
    public static List<String> solveExpressionValue(String expression, Map<String,String> knownVariableValueMap){
        return null;
    }
    
    /**
     * {expression,v1=value, v2=value...}
     * @param logicalExpression
     * @param knownVariableValueMap
     * @return 
     */
    public static Boolean solveLogicalExpressionValue(String logicalExpression,  Map<String,String> knownVariableValueMap){
        return true;
    }
    
    /**
     * calculate the value of the expression based on the given values of the variables;
     * if the given map of variable values are not fully covering all the variables in the expression, how to check!!!!
     * {N(expression),v1=value, v2=value, ...}
     * @param numericExpression
     * @param knownVariableStringValueMap
     * @return null if the given expression can not be calculated to a single numeric value with the given variable values
     */
    public static Double solveNumericExpressionValue(String numericExpression, Map<String,String> knownVariableStringValueMap){
        if(!isValidClosedFormMathSymjaExpressionString(numericExpression)){
            return null;
        }
        
        
        String symjaString = "{N(".concat(numericExpression).concat(")");
        for(String v:knownVariableStringValueMap.keySet()){
            symjaString = symjaString.concat(",").concat(v).concat("=").concat(knownVariableStringValueMap.get(v));
        }
        
        
        symjaString=symjaString.concat("}");
        System.out.println(symjaString);
        
        IExpr result = SYMJA_EVALUATOR.eval(symjaString);
        System.out.println("symja evaluation result:"+result);
//        System.out.println(result.isList());
//        System.out.println(result.isVector());
        
        double[] resultDoubleVector;
        
        try{
            //
            resultDoubleVector = result.toDoubleVector(); 
//            System.out.println(resultDoubleVector);
        }catch(Exception e){
            System.out.println("symja result is not a double vector");
            return null;
        }
        
//        return 1d;
        
        return resultDoubleVector==null? null: resultDoubleVector[0];
    }
   
    
    
    
    
    public static void equationSys(){
        List<String> lineVariableList = new ArrayList<>();
        lineVariableList.add("a");
//        lineVariableList.add("b");
        lineVariableList.add("c");
//        lineVariableList.add("d");
//        lineVariableList.add("e");
//        lineVariableList.add("f");
//        lineVariableList.add("g");
        
        List<String> lineEquationList = new ArrayList<>();
//        lineEquationList.add("(e-a*d*e-b*d*e+2*b*d*f+2*b*d*e*g)/(-1+a*d+b*d)+f == 0");
//        lineEquationList.add("e*g+((f+e*g)*a*d)/(-1+a*d+b*d)==0");
//        lineEquationList.add("-e*h+((-f-e*g)*d)/(-1+a*d+b*d)==0");
//        lineEquationList.add("-e*j+((-f-e*g)*c)/(-1+a*d+b*d)==0");
//        lineEquationList.add("Tan(a) == b^2");
//        lineEquationList.add("c/a+Sqrt(b)== 0");
//        lineEquationList.add("1/a+Tan(a) == 0");
        lineEquationList.add("Tan(a) == 2+c");
        lineEquationList.add("Sin(a) == b^2");
        lineEquationList.add("c == 4.2");
//        lineEquationList.add("c^2-b == Sqrt(a)");
        
        solveEquationsForVariables(lineEquationList,lineVariableList);
        
    }
    public static void lineVariablesAndEquations(){
        List<String> lineVariableList = new ArrayList<>();
        lineVariableList.add("sx");
        lineVariableList.add("sy");
        lineVariableList.add("mx");
        lineVariableList.add("my");
//        lineVariableList.add("ex");
//        lineVariableList.add("ey");
//        lineVariableList.add("t");
//        lineVariableList.add("len");
        
        List<String> lineEquationList = new ArrayList<>();
        lineEquationList.add("sx+ex == 2mx");
        lineEquationList.add("sy+ey == 2my");
        lineEquationList.add("len == sqrt((sx-ex)^2+(sy-ey)^2)");
//        lineEquationList.add("Tan(t) == (ey-sy)/(ex-sx)");
//        lineEquationList.add("Sin(t) == (ey-sy)/len");
//        lineEquationList.add("Cos(t) == (ex-sx)/len");
        
        
        
        
        solveEquationsForVariables(lineEquationList,lineVariableList);
        
        
//        lineEquationList.add("x1+x3 == 2x2");
//        lineEquationList.add("x1+x3 == 2x2");
//        lineEquationList.add("x1+x3 == 2x2");
        
    }
    public static void calExpressionValueByVariable(){
        
    }
    public static void expression(){
        //define an expression with a string
//        String expression = "111+2";
//        toJavaForm(expression);
//        
        String expression2 = "{x+y,{x->1,y->2}}";
        toJavaForm(expression2);
        ExprEvaluator util = new ExprEvaluator();
        IExpr result = util.eval(expression2);
        
        System.out.println(result);
        //define an expression using built-in functions in class org.matheclipse.core.expression.F
//        IAST function1 = Plus(C1,C2);
//        System.out.println(function1);
//        IAST function = Solve(List(Equal(Sqr(x),C4),Equal(Plus(x,Sqr(y)),C6)),List(x,y));
//       
        
    }
    
    /**
     * the equation could contain other variables;
     * @param equationStringList
     * @param variableNameListToBeSolved
     */
    public static void solveEquationsForVariables(List<String> equationStringList, List<String> variableNameListToBeSolved){
        String expressionString = "Solve({";
        for(String equation:equationStringList){
            if(equationStringList.indexOf(equation)==equationStringList.size()-1){
                expressionString = expressionString.concat(equation);
            }else{
                expressionString = expressionString.concat(equation).concat(",");
            }
        }
        
        
        expressionString = expressionString.concat("},{");
        for(String variable:variableNameListToBeSolved){
            if(variableNameListToBeSolved.indexOf(variable)==variableNameListToBeSolved.size()-1){
                expressionString = expressionString.concat(variable);
            }else{
                expressionString = expressionString.concat(variable).concat(",");
            }
        }
        expressionString = expressionString.concat("})");
        
        System.out.println(expressionString);
        
        
        ExprEvaluator util = new ExprEvaluator();
        
        IExpr result = util.eval(expressionString);
        System.out.println(result);
//        
//        ExprEvaluator util = new ExprEvaluator();
//        IExpr result2 = util.eval("Solve({x^2==4,x+y^2==6}, {x,y})");
//        System.out.println(result2);
//        
//        
//        
//        System.out.println(result);
//        System.out.println(result.getAt(0));
//        System.out.println(result.getAt(1));
//        System.out.println(result.getAt(1).getAt(0));
//        System.out.println(result.getAt(1).getAt(1));
//        System.out.println(result.getAt(1).getAt(1).getAt(0));
//        System.out.println(result.getAt(1).getAt(1).getAt(1));
//        System.out.println(result.getAt(1).getAt(1).getAt(2));
//        
//        
//        toJavaForm("Solve({x^2==4,x+y^2==6}, {x,y})");
    }
    
    /**
     * given an expression, transform it into java 
     * @param expression 
     */
    public static void toJavaForm(String expression){
        ExprEvaluator util = new ExprEvaluator(); 
        String javaForm = util.toJavaForm(expression);
        System.out.println(javaForm);
    }
    
    
    public static void main(String[] args){
//        toJavaForm("1+1");
//        solveForSingleVariableInAnEquation(null,null);


//        Map<String,Number> variableValueMap = new HashMap<>();
//        variableValueMap.put("x", 1d);
//        variableValueMap.put("y", 1d);
//        variableValueMap.put("z", 1d);
//        variableValueMap.put("a", 10);
//        variableValueMap.put("b", Math.PI/2);
//        String expression = "x^a+b/y-tan(z)/arcsin(b)*sqrt(a!)";
//        
//        Double result = solveExpressionValue(expression,variableValueMap);
//        
//        System.out.println(result);
        
        
        
//        System.out.println(extractAtomNonBuiltInStringComponentSet("abs(x_a^a)+b/y-tan(z)/arcsin(b/c*f+w^s)*sqrt(a!)"));
        

//        IExpr i = SYMJA_EVALUATOR.eval("a1");
//        System.out.println(i.isVariable());
//        System.out.println(isValidSymjaExpressionString(" a_d"));
        

        //a_1 is not valid variable name;
        //a_a is not valid variable name;
//        System.out.println(isValidUserDefinedSymjaSymbolName("a_1"));
        
        System.out.println(isVariable("1"));
        
//        System.out.println(isValidUserDefinedSymjaSymbolName("a_a"));
//        String expression = "a_a^2-10";
//        Map<String,Double> variableValueMap = new HashMap<>();
//        variableValueMap.put("a_a", 1d);
//        System.out.println(solveExpressionValue(expression,variableValueMap));
        
    }
}
