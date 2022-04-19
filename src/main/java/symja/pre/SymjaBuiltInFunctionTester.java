/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symja.pre;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

/**
 *
 * @author tanxu
 */
public class SymjaBuiltInFunctionTester {
    private final static ExprEvaluator SYMJA_EVALUATOR = new ExprEvaluator();
    
    
    
    
    public static void abs(){
        
//        IExpr result = SYMJA_EVALUATOR.eval("{N(abs(v+w)),v=10, w=-1000}");
//        IExpr result = SYMJA_EVALUATOR.eval("{N(abs(v+w)),v=10, w=true, k=true}");
//        IExpr result = SYMJA_EVALUATOR.eval("{a&&b, a=true, b=2>1}");
        
        IExpr result = SYMJA_EVALUATOR.eval("{a==\"2fadfaf\", a=\"2fadfaf\", b=2>1}");
        System.out.println(result);
        System.out.println(result.getAt(1));
        
        
//        System.out.println(SymjaUtils.extractNonBuiltInVariableNameSet("{N(abs(v+w-a)),v=10, w=-1000, k=true}"));
    }
    
    
    
    public static void variables(){
        String expression = "xdfa111^a+b/y-tan(z)/arcsin(b^c)*sqrt(a!)";
        
        
        
        IExpr result = SYMJA_EVALUATOR.eval("Variables("+expression+")");
        System.out.println(result);
        
        
        
        IExpr variable = result.getAt(1);
        System.out.println(variable);
        System.out.println(variable.leafCount());
        System.out.println(variable.getAt(0));
        System.out.println(variable.getAt(0).isAtom());
        System.out.println(variable.getAt(1));
        System.out.println(variable.getAt(1).isAtom());
//        System.out.println(variable.getAt(1).getAt(0));
    }
    
    
    
    /**
     * solve function is quite unstable, it might produce solution for a non-solution set of variable; 
     * or produce no solution for a solution set of variables;
     * 
     */
    public static void solve(){
//        String expression = "Solve({2*x + 3*y == 4, 3*x - 4*y <= 5,x - 2*y > -21}, {x,  y}, Integers)";
//        String expression1 = "Solve({x^2==4c,Tan(x)+Sqrt(y)==6d}, {x,y})";
//        
//        //solvable, but l can be negative
//        String expression2 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, l == Sqrt((x3-x1)^2+(y3-y1)^2)}, {x1, y1, l})"; 
//        
//        //x1, y1 are solved, l is given based on x1 and y1
//        String expression21 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, l == Sqrt((x3-x1)^2+(y3-y1)^2), x3=1, x2 = 5, y3 = -1, y2 = 33}, {x1, y1, l})"; 
//        
//        //unsolvable
//        String expression3 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, l == Sqrt((x3-x1)^2+(y3-y1)^2), Tan(t) == (y3-y1)/(x3-x1) }, {x1, y1, l, t})"; 
//        
//        //solvable
//        String expression31 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, l == Sqrt((x3-x1)^2+(y3-y1)^2), t == arctan((y3-y1)/(x3-x1)) }, {x1, y1, l, t})";
//        String expression311 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, l == Sqrt((x3-x1)^2+(y3-y1)^2), tan(t) == (y3-y1)/(x3-x1) }, {x1, y1, l, t})";
//        String expression32 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, l == Sqrt((x3-x1)^2+(y3-y1)^2), t == arcsin((y3-y1)/l) }, {x1, y1, l, t})";
//        String expression33 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, l == Sqrt((x3-x1)^2+(y3-y1)^2), t == arccos((x3-x1)/l) }, {x1, y1, l, t})";
//        
//        String expression4 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, l == Sqrt((x3-x1)^2+(y3-y1)^2), t == arctan((y3-y1)/(x3-x1)), t == arcsin((y3-y1)/l),t == arccos((x3-x1)/l)}, {x1, y1, t, l})"; 
//        String expression41 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, l == Sqrt((x3-x1)^2+(y3-y1)^2), tan(t) == (y3-y1)/(x3-x1), sin(t) == (y3-y1)/l, cos(t) == (x3-x1)/l}, {x1, y1, t, l})";
        
        
        //solvable
        String expression1 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, sin(t) == (y3-y1)/l, cos(t) == (x3-x1)/l}, {x2, x3, y2, y3})"; 
        String expression2 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, sin(t) == (y3-y1)/l, cos(t) == (x3-x1)/l}, {x1, x2, y1, y2})"; 
        
        
        //unsolvable
        String expression3 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, sin(t) == (y3-y1)/l, cos(t) == (x3-x1)/l}, {x2, y1, l, t})"; 
        
        //solvable
        //{{l->-(2*x1-2*x2)*Sec(t),x3->-x1+2*x2,y2->1/2*(2*y1-2*x1*Tan(t)+2*x2*Tan(t)),y3->-Sec(t)*(-y1*Cos(t)+2*x1*Sin(t)-2*x2*Sin(t))}}
        String expression4 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, sin(t) == (y3-y1)/l, cos(t) == (x3-x1)/l}, {x3, y2, y3, l})";
        
        //???
        String expression5 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, sin(t) == (y3-y1)/l, cos(t) == (x3-x1)/l}, {y1, y3,x3,  l})";
        
        //solvable
        //{{l->-(2*x1-2*x2)*Sec(t),x3->-x1+2*x2,y1->Sec(t)*(y3*Cos(t)+2*x1*Sin(t)-2*x2*Sin(t)),y2->1/2*(2*y3+2*x1*Tan(t)-2*x2*Tan(t))}}
        //{{l->2*(-x1*Sec(t)+x2*Sec(t)),x3->-x1+2*x2,y1->y3+2*x1*Tan(t)-2*x2*Tan(t),y2->y3+x1*Tan(t)-x2*Tan(t)}}
        String expression6 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, sin(t) == (y3-y1)/l, cos(t) == (x3-x1)/l}, {x3, y1, y2, l})";
        
        
        String expression7 = "Solve({x1+x3 == 2x2, y1+y3 == 2y2, sin(t) == (y3-y1)/l, cos(t) == (x3-x1)/l}, {x3, y1, y2, y3})";
//        String expression4 = "Solve({t})";
        

        
        
        ExprEvaluator util = new ExprEvaluator();
        
        IExpr result = util.eval(expression7);
        
        System.out.println(result);
        
        
        
        
        
    }
    
    
    public static void tan(){
        ExprEvaluator util = new ExprEvaluator();
        IExpr result = util.eval("N(tan(pi/2))");
        System.out.println(result);
        
//        double total = 10;
//        for(int i=0;i<=total;i++){
//            
//            System.out.print(i/total+"*2pi\t");
//            System.out.println(util.eval("tan("+i+"*2*Pi/"+total+")"));
//        }
//        System.out.println(result);
    }
    
    
    /**
     * arctan(x), x in (-infinite,+infinite)
     */
    public static void arctan(){
        ExprEvaluator util = new ExprEvaluator();
        IExpr result = util.eval("arctan(1)");
        System.out.println(result);
        
        
//        double total = 10;
//        for(int i=-100;i<=100;i+=10){
//            
//            System.out.print(i+"\t");
//            System.out.println(util.eval("N(arctan("+i+")/Pi)")+"Pi");
//        }
    }
    
    /**
     * domain (-i,+i)
     */
    public static void sin(){
        ExprEvaluator util = new ExprEvaluator();
        IExpr result = util.eval("N(sin(-10000000000000))");
        System.out.println(result);
        
//        double total = 10;
//        for(int i=0;i<=total;i++){
//            
//            System.out.print(i/total+"*2pi\t");
//            System.out.println(util.eval("tan("+i+"*2*Pi/"+total+")"));
//        }
//        System.out.println(result);
    }
    
    
    /**
     * arcsin(x), domain [-1,1], codomain [-pi/2, pi/2]
     */
    public static void arcsin(){
        ExprEvaluator util = new ExprEvaluator();
        IExpr result = util.eval("arcsin(sqrt(2)/2)");
        System.out.println(result);
        
        
//        double total = 10;
//        for(int i=0;i<=10;i++){
//            double x = -1+i*2/total;
//            System.out.print(x+"\t");
//            System.out.println(util.eval("N(arcsin("+x+")/Pi)")+"Pi");
//        }
    }
    
    /**
     * domain (-i,+i)
     */
    public static void cos(){
        ExprEvaluator util = new ExprEvaluator();
        IExpr result = util.eval("N(cos(-10000000000000))");
        System.out.println(result);
        
//        double total = 10;
//        for(int i=0;i<=total;i++){
//            
//            System.out.print(i/total+"*2pi\t");
//            System.out.println(util.eval("tan("+i+"*2*Pi/"+total+")"));
//        }
//        System.out.println(result);
    }
    
     /**
     * arcsin(x), domain [-1,1], codomain [-pi/2, pi/2]
     */
    public static void arccos(){
        ExprEvaluator util = new ExprEvaluator();
        IExpr result = util.eval("arccos(sqrt(2)/2)");
        System.out.println(result);
        
        /**
         * return a complex number if out of domain
         */
//        IExpr result2 = util.eval("RealNumberQ(N(arccos(10)))");
//        System.out.println(result2);
//        
//        double total = 10;
//        for(int i=0;i<=10;i++){
//            double x = -1+i*2/total;
//            System.out.print(x+"\t");
//            System.out.println(util.eval("N(arccos("+x+")/Pi)")+"Pi");
//        }
    }
    
    
    public static void main(String[] args){
//        arccos();
//        variables();
        abs();
    }
}
