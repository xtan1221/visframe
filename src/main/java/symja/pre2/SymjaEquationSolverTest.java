/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symja.pre2;

import static symja.pre2.SymjaUtils2Test.SYMJA_EVALUATOR;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

/**
 *
 * @author tanxu
 */
public class SymjaEquationSolverTest {
    
    public static void test4(){
//        String exp = "{a<11 && c>10, a = c+10, c=221}";
//        String exp = "NSolve({x1+x3==2x2, y1+y2==2y3, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L, x1 = 1, y1 = 5, L = 10, t=PI/3}, {x2, x3, y2, y3})";
//        String exp = "NSolve({x1+x3==2x2, y1+y2==2y3, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L, x1=0, y1 = 10, t = 2.3, L=3}, {x2, x3, y2, y3})";
        String exp = "NSolve({x1+x3==2x2, y1+y2==2y3, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L}, {x2, x3, y2, y3})";
//        String exp = "{x1+x3==2x2, y1+y2==2y3, sin(t)==(y3-y1)/L, cos(t)==(x3-x1)/L, x1=0, y1 = 10, t = 2.3, L=3}";
//        String exp = "{x3==2*x2,10+y2==2*y3,y3=12.237115636530161,x3=-1.9988280638394724}";
//        String exp = "{x3-2*x2==0,10+y2-2*y3==0,y3=12.237115636530161,x3=-1.9988280638394724}";
//        String exp = "NSolve({x1+x3==2x2, y1+y2==2y3, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L, x1 = 1, y1 = 5, L = 10, t=PI/3}, {x2, x3, y2, y3})";
       
//        String exp = "{x1+x3==2x2, y1+y2==2y3, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L, x1 = 1, y1 = 5, L = 10, t=PI/3}";
//        String exp = "{Sin(t)+Cos(t)==0, Sin(t)^2+Cos(t)^2 == 1}";
//        String exp = "NSolve({Sin(t)+Cos(t)==0, Sin(t)^2+Cos(t)^2 == 1},{Sin(t),cos(t)})";
//        String exp = "{a+b==0, a^2+b^2 == 1}";
//        String exp = "NSolve({a+b==0, a^2+b^2 == 1},{a,b})"; //solvable
        
//        ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
//        IExpr result2 = parser.parse(exp);
        IExpr result2 = SYMJA_EVALUATOR.eval(exp);
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
    
    
    public static void testWithValues(){
//        String exp = "NSolve({x1+x3==2x2, y1+y2==2y3, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L}, {x2, x3, y1, y3})";
        String exp = "NSolve({x1+x3==2x2, y1+y2==2y3, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L, x1=0, y2 = 10, L = 10, t = 3.14}, {x2, x3, y1, y3})";
        IExpr result1 = SYMJA_EVALUATOR.eval(exp);
        System.out.println(result1); //NSolve({x3==2*x2,10+y1==2*y3,-y1+y3==0.01592652916486828,x3==-9.999987317275394,0,10,10,3.14},{x2,x3,y1,y3})
        
        String exp2 = "NSolve({10+y1==2*y3,-y1+y3==0.01592652916486828},{y1, y3})";
        IExpr result2 = SYMJA_EVALUATOR.eval(exp2);
        System.out.println(result2);//{{y1->9.968146941670263,y3->9.984073470835131}}
    }
    
    
    public static void test2(){
        String exp = "NSolve({x1+x3==2x2, y1+y2==2y3, sin(t) ==(y3-y1)/L, cos(t) ==(x3-x1)/L}, {x2, x3, y1, y3})";
        IExpr result1 = SYMJA_EVALUATOR.eval(exp);
        System.out.println(result1); //{{}}
        
        String exp2 = "NSolve({x1+x3==2x2, y1+y2==2y3, sin(t) ==(y3-y1)/L}, {x2, x3, y1, y3})";
        IExpr result2 = SYMJA_EVALUATOR.eval(exp2);
        System.out.println(result2); //{{x2->0.5*x1+0.5*x3}}
        
        String exp3 = "NSolve({x1+x3==2x2, y1+y2==2y3, cos(t) ==(x3-x1)/L}, {x2, x3, y1, y3})";
        IExpr result3 = SYMJA_EVALUATOR.eval(exp3);
        System.out.println(result3); //{{}}
    }
    
    
    public static void main(String[] args){
        test2();
    }
}
