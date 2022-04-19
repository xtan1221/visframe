/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symja.pre;

import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.DoubleVariable;
import org.matheclipse.parser.client.eval.IDoubleValue;

/**
 *
 * https://github.com/axkr/symja-parser
 * @author tanxu
 */
public class SymjaParserTester {
    public static void test(){
        DoubleEvaluator engine = new DoubleEvaluator(true);
        
        
        IDoubleValue vd1 = new DoubleVariable(3.0);
        engine.defineVariable("X", vd1);
        
        IDoubleValue vd2 = new DoubleVariable(3.0);
        engine.defineVariable("Y", vd2);
        
        double d = engine.evaluate("X^2+3Y");
        
        System.out.println(d);
        vd1.setValue(4);
        d = engine.evaluate();
        System.out.println(d);
        
        
        
        
    }
    
    public static void test2(){
        String expression = "";
        
        DoubleEvaluator engine = new DoubleEvaluator(true);
        
    }
    
    
    
    
    
    public static void main(String[] args){
        test();
    }
    
    
    
    
}
