/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symja.pre2;

import java.util.Map;
//import static org.jooq.impl.DSL.exp;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.parser.ExprParserFactory;

/**
 *
 * @author tanxu
 */
public class SymjaUtils2Test {
    static ExprEvaluator SYMJA_EVALUATOR = new ExprEvaluator();
    
    public static void test(){
//        String expression = "a__))";
//        String expression = "a__C:..,";
//        String expression = "1==1 || 2>2";
//        String expression  = "a<1 and b>2";
        String expression = "{x1+x3==2x2, y1+y2==2y2, sin(t) == (y3-y1)/L, cos(t) == (x3-x1)/L, x1 = 1, x2 = 5}";
//        String expression = "x1+sin(x2-x4)/rs+b-tsst(2323)y2/y3";
        SymjaUtils.checkSyntaxOfExpressionString(expression);
        Map<String,Boolean> v = SymjaUtils.findOut0IndexASTItems(expression);
        
        System.out.println("======================================");
        for(String n:v.keySet()){
            System.out.println("0-indexed ast string: "+n+", is built in symbol?"+v.get(n));
        }
        
        System.out.println("======================================");
        Map<String, Boolean> atomIndex = SymjaUtils.findOutAllAtomASTWithNon0Index(expression);
        for(String n:atomIndex.keySet()){
            System.out.println("non-0 indexed atom ast string: "+n+",  is variable?:"+atomIndex.get(n));
        }
    }
    
    
    public static void test2(){
//        String expression = "x1+sin(x2-x4)/rs+b-tsst(2323)y2/y3";
//        String expression = "and(a+b>3,dd=2)";
        String expression = "a+c-11";
//        String expression = "OR(1==1,2>2)";
//        String expression = "1==1 || 2>2";
        final ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
        IExpr exp = parser.parse(expression);
        
//        IExpr result2 = SYMJA_EVALUATOR.eval("{a+c-10, a = 10, c=1}");
        
//        System.out.println(SymjaUtils2.firstLevelASTFunctionIsSimpleLogic(expression));
        
//        System.out.println(result2.isNumeric());
//        System.out.println(result2.isNumber());
//        
//        if(result.isAST()){
//            IExpr f = result.getAt(0);
//            System.out.println(f.toString());
//            System.out.println(f.isCondition());
//            System.out.println(f.isConditionalExpression());
//            
//        }
    }
    
    
    public static void test3(){
//        String exp = "{a<11 && c>10, a = c+10, c=221}";
//        String exp = "NSolve({x1+x3==2x2, y1+y2==2y3, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L, x1 = 1, x3 = 5, y1 = 0, y3=-4}, {x2, y3, t, L})";
//        String exp = "{x1+x3==2x2, y1+y2==2y3, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L, x1 = 1, x3 = 5, y1 = 0, y3=-4}";
//        String exp = "{Sin(t)+Cos(t)==0, Sin(t)^2+Cos(t)^2 == 1}";
//        String exp = "NSolve({Sin(t)+Cos(t)==0, Sin(t)^2+Cos(t)^2 == 1},{Sin(t),cos(t)})";
//        String exp = "{a+b==0, a^2+b^2 == 1}";
        String exp = "NSolve({a+b==0, a^2+b^2 == 1},{a,b})"; //solvable
        
        ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
        IExpr result2 = parser.parse(exp);
        
        System.out.println(result2);
        System.out.println(result2.getAt(1));
        System.out.println(result2.getAt(1).getAt(1));
        System.out.println(result2.getAt(1).getAt(1).getAt(2));
        
        IExpr result3 = F.ArcSin(result2.getAt(1).getAt(1).getAt(2));
        System.out.println(result3.evalReal().divide(F.Pi));
        System.out.println(result3.evalReal().divide(F.Pi).evalReal().plus(F.integer(2)));
        
        IExpr result4 = F.ArcCos(result2.getAt(1).getAt(2).getAt(2));
        System.out.println(result4.evalReal().divide(F.Pi));
        System.out.println(result4.evalReal().divide(F.Pi).evalReal().plus(F.integer(2)));
        
//        
        
//        IExpr a0 = result2.getAt(1);
//        System.out.println(a0.toString());
//        System.out.println(a0.isBooleanResult());
    }
    
    
    public static void test4(){
//        String exp = "{a<11 && c>10, a = c+10, c=221}";
//        String exp = "NSolve({x1+x3==2x2, y1+y2==2y3, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L, x1 = 1, y1 = 5, L = 10, t=PI/3}, {x2, x3, y2, y3})";
        String exp = "NSolve({x1+x3==2x2, y1+y2==2y3, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L}, {x2, x3, y2, y3})";
//        String exp = "{x1+x3==2x2, y1+y2==2y3, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L, x1 = 1, y1 = 5, L = 10, t=PI/3}";
//        String exp = "{Sin(t)+Cos(t)==0, Sin(t)^2+Cos(t)^2 == 1}";
//        String exp = "NSolve({Sin(t)+Cos(t)==0, Sin(t)^2+Cos(t)^2 == 1},{Sin(t),cos(t)})";
//        String exp = "{a+b==0, a^2+b^2 == 1}";
//        String exp = "NSolve({a+b==0, a^2+b^2 == 1},{a,b})"; //solvable
        
        ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
        IExpr result2 = parser.parse(exp);
        
        System.out.println(result2);
//        System.out.println(result2.getAt(1));
//        System.out.println(result2.getAt(1).getAt(1));
//        System.out.println(result2.getAt(1).getAt(1).getAt(2));
//        
//        IExpr result3 = F.ArcSin(result2.getAt(1).getAt(1).getAt(2));
//        System.out.println(result3.evalReal().divide(F.Pi));
//        System.out.println(result3.evalReal().divide(F.Pi).evalReal().plus(F.integer(2)));
//        
//        IExpr result4 = F.ArcCos(result2.getAt(1).getAt(2).getAt(2));
//        System.out.println(result4.evalReal().divide(F.Pi));
//        System.out.println(result4.evalReal().divide(F.Pi).evalReal().plus(F.integer(2)));
        
//        
        
//        IExpr a0 = result2.getAt(1);
//        System.out.println(a0.toString());
//        System.out.println(a0.isBooleanResult());
    }
    
    
    public static void test5(){
//        String exp1 = "NSolve({x1+x3==2x2, y1+y3==2y2, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L}, {x2, x3, y2, y3})"; //solvable for each variable
//        String exp2 = "NSolve({x1+x3==2x2, y1+y3==2y2, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L}, {x2, x3, y1, y3})"; 
//        String exp2 = "NSolve({x1+x3==2x2, y1+y3==2y2, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L}, {x2, x3, y1, y2})"; 
//        String exp2 = "NSolve({x1+x3==2x2, y1+y3==2y2, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L}, {x1, x3, y2, y3})"; 
//        String exp2 = "NSolve({x1+x3==2x2, y1+y3==2y2, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L}, {x1,x2, y1,y2})";
        
//        String exp2 = "NSolve({x1+x3==2x2, y1+y3==2y2, sin(t)==(y3-y1)/L, cos(t)==(x3-x1)/L}, {x2, y2})";
        String exp2 = "{a>b, a=10, b=1}";
        ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
//        IExpr result2 = parser.parse(exp2);
        
        IExpr result2 = SYMJA_EVALUATOR.eval(exp2);
        
        System.out.println(result2);
        
        System.out.println(result2.getAt(0).isBooleanResult());
    }
    
    public static void main(String[] args){
        test5();
    }
}
