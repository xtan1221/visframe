/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symja.pre2;

import org.matheclipse.core.eval.ExprEvaluator;

/**
 *
 * @author tanxu
 */
public class SymjaEvaluatorUtils {
    //note that to evaluate a symja expression string, have to use the ExprEvaluator object
    //the ExprParser can not be used to evaluate symja expression string!!!!!!!
    //  ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
    //  IExpr result2 = parser.parse(exp2);
    protected final static ExprEvaluator SYMJA_EVALUATOR = new ExprEvaluator();
    
    
    
    
}
