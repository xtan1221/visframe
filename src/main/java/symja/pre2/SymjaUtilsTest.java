/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symja.pre2;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.parser.ExprParserFactory;

import symja.pre.SymjaUtils_old;

/**
 *
 * @author tanxu
 */
public class SymjaUtilsTest {
    
    public static void test(){
        System.out.println(SymjaUtils_old.isBuiltInSymbol("D(a+b-sin(23))+hello(2)"));
//        System.out.println(SymjaUtils.isBuiltInSymbol("hello(2)"));
//        System.out.println(SymjaUtils.isBuiltInSymbol("hello(2)"));
//        System.out.println(SymjaUtils.isBuiltInSymbol("'hello'"));
    }
    
    
    public static void test2(){
        String expression = "D(a-b,1)";//!isFunction
//        String expression = "Hello(a)"; // isAST
//        String expression = "a+b";//isPlus, isAST
//        String expression  = "a";//!isValue, 
//        String expression = "11";//!isValue, isNumber
//        String expression = "1==1";//isBooleanResult
//        String expression = "1<2&3>A";//isBooleanResult
//        String expression = "1<2&&3>A";//isBooleanResult
//        String expression = "And(1<2,3>A)";//isBooleanResult
//        String expression = "\"aaaa\"";//isString
//        String expression = "aaaa";//!isString
//        String expression = "Hello(2)";//!isFunction
//        String expression = "OR(2,2)";//isOr
//        String expression = "OR(2)";//!isOr
//        IExpr result = SYMJA_EVALUATOR.eval(expression);
        
        final ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
		IExpr result = parser.parse(expression);
        
        System.out.println(result.isFunction());
        
    }
    
    public static void testAST(){
        String expression = "N(2.1)-asd";
        
        ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
        IExpr result = parser.parse(expression);
        
        if(result.isAST()){
            IExpr child = result.getAt(0);
            System.out.println(child.toString());
            System.out.println(child.isBuiltInSymbol());
        }else{
            System.out.println("isAtom:"+result.isAtom());
        }
        
        //atom: a expression with 
        //D(2,2), N(2)
        
        //ast: a tree structure with a root and children nodes
        //a+b, dfafaf(232), N(2.1)-asd
    }
    
    public static void test_firstLevelASTFunctionIsSimpleLogic(){
//        String expression = "x>4";//Greater
//        String expression = "x>=a-da*4";//GreaterEqual, 
//        String expression = "x>=a-4";//GreaterEqual, 
//        String expression = "x>=aaa";//GreaterEqual, 
//        String expression = "a=5";
//        String expression = "a+3=5-b";
//        String expression = "a+3==5-b";
        String expression = "a+b*sin(t)-x^y";
//        String expression = "x>=a";//GreaterEqual, 
//        String expression = "x+3-s/211*tan(ssfds)==4";//Equal
//        String expression = "x+3-s/211";//Equal
        ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
        IExpr result = parser.parse(expression);
        
        
        System.out.println(SymjaUtils.firstLevelASTFunctionIsSimpleLogic(expression));
//        if(result.isAST()){
//            IExpr child = result.getAt(0);
//            System.out.println(child.toString());
////            System.out.println(child.isBuiltInSymbol());
//            System.out.println(result.isBooleanResult());
//            System.out.println(child.isBooleanResult());
//            System.out.println(result.isComparatorFunctionSymbol());
//            System.out.println(child.isComparatorFunctionSymbol());
////            System.out.println(child.iseq
//        }else{
//            System.out.println("isAtom:"+result.isAtom());
//        }
        
        //atom: a expression with 
        //D(2,2), N(2)
        
        //ast: a tree structure with a root and children nodes
        //a+b, dfafaf(232), N(2.1)-asd
    }
    public static void testAST3(){
//        String expression = "x>4";//Greater
//        String expression = "x>=4";//GreaterEqual
        String expression = "x+1-a";//
        ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
        IExpr result = parser.parse(expression);
        
        
//        System.out.println(SymjaUtils2.firstLevelASTFunctionIsSimpleLogic(expression));
        if(result.isAST()){
            IExpr child = result.getAt(0);
            System.out.println(child.toString());
            System.out.println(child.isBuiltInSymbol());
            System.out.println(result.isBooleanResult());
            System.out.println(child.isBooleanResult());
            System.out.println(result.isNumber());
            System.out.println(child.isNumeric());
        }else{
            System.out.println("isAtom:"+result.isAtom());
        }
        
        //atom: a expression with 
        //D(2,2), N(2)
        
        //ast: a tree structure with a root and children nodes
        //a+b, dfafaf(232), N(2.1)-asd
    }
    
    public static void main(String[] args){
        test_firstLevelASTFunctionIsSimpleLogic();
    }
}
