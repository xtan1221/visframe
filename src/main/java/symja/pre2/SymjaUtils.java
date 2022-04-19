/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symja.pre2;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.parser.ExprParserFactory;

import basic.SimpleName;


/**
 *
 * @author tanxu
 */
public class SymjaUtils {
    
//    protected final static ExprEvaluator SYMJA_EVALUATOR = new ExprEvaluator();
    
    
    public static double evaluateNumericExpression(String exp,Map<SimpleName,String> variableNameStringValueMap){
        
        
        
        return 0;
    }
    
    
    public static boolean isAtom(String expression){
        try {
            final ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
            IExpr result = parser.parse(expression);
            return result.isAtom();

        } catch (org.matheclipse.parser.client.SyntaxError e) {
            System.out.println("Basic format error:" + e.getMessage());
            return false;
        }

    }
    
    public static boolean isAst(String expression){
        try {
            final ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
            IExpr result = parser.parse(expression);
            return result.isAST();

        } catch (org.matheclipse.parser.client.SyntaxError e) {
            System.out.println("Basic format error:" + e.getMessage());
            return false;
        }
    }
    
    /**
     * return true if the given expression string is contains a = symbol at the first level ast with index 0, which assign a value to a variable;
     * a=4
     * a = 5+c
     * b-2 = sin(r) -2
     * @param expression
     * @return 
     */
    public static boolean isSetValueExpression(String expression){
        try {
            final ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
            IExpr result = parser.parse(expression);
            
            if(result.isAST()){
                IExpr firstLevel0IndexedAstChild = result.getAt(0);
                System.out.println(firstLevel0IndexedAstChild.toString());
                //when the expression is a set value to variable string, the firstLevel0IndexedAstChild.isBooleanResult will return true, which is weired;
                //for example, a = 4
                return firstLevel0IndexedAstChild.toString().equalsIgnoreCase("Set");//
            }else{
                System.out.println("atom");
                throw new IllegalArgumentException("given expression string is not ast");
            }
        } catch (org.matheclipse.parser.client.SyntaxError e) {
            System.out.println("Basic format error:" + e.getMessage());
            return false;
        }
    }
    
    
    /**
     * return true if the AST string at the first level with index 0 is either AND, OR, NOT;
     * 
     * or first level ast with index 0 is boolean comparison (isBooleanResult()) such as >, <, >=, <=, ==,
     * >NOTE that  this is a loose validation and passing this validation does not guarantee the expression is a valid logic expression!
     * @param expression
     * @return 
     */
    public static boolean firstLevelASTFunctionIsSimpleLogic(String expression){
        try {
            final ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
            IExpr result = parser.parse(expression);
            
            if(result.isAST()){
                IExpr firstLevel0IndexedAstChild = result.getAt(0);
                System.out.println(firstLevel0IndexedAstChild.toString());
                
                return result.isNot()||result.isOr()||result.isAnd()
                            ||firstLevel0IndexedAstChild.isComparatorFunctionSymbol() //this will include all the comparison operators
                            ;
            }else{
                System.out.println("atom");
                throw new IllegalArgumentException("given expression string is not ast");
            }
            
            
        } catch (org.matheclipse.parser.client.SyntaxError e) {
            System.out.println("Basic format error:" + e.getMessage());
            return false;
        }
    }
    
    /**
     * find out whether there is any syntax error in the expression string; not
     * check for variables and constant validation;
     *
     * @param expression
     * @return
     */
    public static boolean checkSyntaxOfExpressionString(String expression) {
        try {
            final ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
            IExpr result = parser.parse(expression);

//            System.out.println(result.toScript());
            return true;
        } catch (org.matheclipse.parser.client.SyntaxError e) {
            System.out.println("Basic format error:" + e.getMessage());
            return false;
        }
    }
    
    /**
     * find out all the variable names at the 0 index of each ast in the expression;
     * https://github.com/axkr/symja_android_library/wiki/Class-hierarchy
     * The Symja parser maps the source code of math functions (like Sin(x), a+b+c, PrimeQ(17),...) in a tree form called Abstract Syntax Tree. 
     * These functions are represented as AST objects (derived from the IAST and IExpr interfaces). 
     * The head (i.e. Sin, Plus, PrimeQ,...) of a function is stored at index 0 in the list. 
     * The n arguments of the function are stored in the indexes 1..n.
     * @param expression
     * @return map of ast name and a boolean value indicating wether it is a symja builtin symbol or not; normally 0 index ast should be a builtin function or constant, if not, must be careful and check what it is
     */
    public static Map<String,Boolean> findOut0IndexASTItems(String expression) {
        Map<String,Boolean> ret = new HashMap<>();
        
        if(checkSyntaxOfExpressionString(expression)){
            final ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
            IExpr result = parser.parse(expression);
            
            if(result.isAST()){
//                System.out.println("find ast 0 indexed item:"+result.getAt(0).toString());
                ret.put(result.getAt(0).toString(),result.getAt(0).isBuiltInSymbol());
            }
            
            for(int i=1;i<result.size();i++){
                ret.putAll(findOut0IndexASTItems(result.getAt(i).toString()));
            }
            
            
            return ret;
            
        }else{
            throw new IllegalArgumentException("given expression has syntax error");
        }
        
    }

    
    /**
     * return all the atom ast in the given expression with index != 0;
     * normally, all and only constant value and variables should be included, and no function names should be included;
     * 
     * @param expression
     * @return a map from atom ast string to boolean value indicating whether the ast atom string is a variable or not (constant value) 
     */
    public static Map<String, Boolean> findOutAllAtomASTWithNon0Index(String expression){
        Map<String,Boolean> astNameIndex = new HashMap<>();
        if(checkSyntaxOfExpressionString(expression)){
            final ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
            IExpr result = parser.parse(expression);
            
            if(result.isAST()){
                for(int i=1;i<result.size();i++){
                    IExpr child = result.getAt(i);
                    if(child.isAtom()){   
                        astNameIndex.put(child.toString(),child.isVariable());
                    }else if(child.isAST()){
                        astNameIndex.putAll(findOutAllAtomASTWithNon0Index(child.toString()));
                    }else{
//                        System.out.println("expression is not AST nor Atom (unexpected, check!!!!):"+expression);
                        throw new IllegalArgumentException("expression is not AST nor Atom (unexpected, check!!!!):"+expression);
                    }
                }
                
            }else if(result.isAtom()){
                System.out.println("the input expression string is atom!");
                astNameIndex.put(result.toString(), result.isVariable());
            }else{
//                System.out.println("expression is not AST nor Atom (unexpected, check!!!!):"+expression);
                throw new IllegalArgumentException("expression is not AST nor Atom (unexpected, check!!!!):"+expression);
            }

            
            return astNameIndex;
            
        }else{
            throw new IllegalArgumentException("given expression has syntax error");
        }
    }
}
