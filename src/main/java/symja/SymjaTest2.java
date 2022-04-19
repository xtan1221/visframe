package symja;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.parser.ExprParserFactory;

public class SymjaTest2 {

	
	/**
	 * identify the data type of an expression string
	 */
	public static void test() {
//		String expression = "(a+b)"; //first level 0 indexed AST is PLUS, isNumericFunction returns false
		String expression = "Factor(a+b)"; //isFunction returns false!!?????? Weird
//		String expression = "adfsfb";
		final ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
        IExpr result = parser.parse(expression);
        
        if(result.isAST()){
            IExpr firstLevel0IndexedAstChild = result.getAt(0);
            System.out.println(firstLevel0IndexedAstChild.isNumericFunction());
            System.out.println(firstLevel0IndexedAstChild.isFunction());
            System.out.println(firstLevel0IndexedAstChild.isAtom());
            System.out.println(firstLevel0IndexedAstChild.isBuiltInSymbol());
            System.out.println(firstLevel0IndexedAstChild.isString());
            System.out.println(firstLevel0IndexedAstChild.toString());
        }
		
        
        
	}
	
	
	public static void main(String[] args) {
		test();
	}
	
	
}
