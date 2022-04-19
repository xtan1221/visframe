package symja;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

public class SymjaTest {
	public static void testValidateExpressionString() {
//		String expr = "{a+b*5/c^4, a=5, b = 1, c=2";
//		String expr = "a+b*5/c^4sfd";
		
		String expression = "{prc+1,prc=1.0}";
		
		
		ExprEvaluator util = new ExprEvaluator();
		IExpr result = util.eval(expression);
		
		System.out.println(result.getAt(1).toString());
		Double value = result.getAt(1).evalDouble();
		System.out.println(value);
		System.out.println(result.getAt(1).toString());
	}
	public static void testEvaluateNumericExpressionWithAliasVariables() {
		String expr = "{a+b*5/c^4sfd, a=5, b = 1, c=2, sfd = 2}";
		
		ExprEvaluator util = new ExprEvaluator();
		IExpr result = util.eval(expr);
		System.out.println(result);
//		IExpr expr = util.evaluate("x^2+y+a*x+b*y+c");
		
		Double value = result.getAt(1).evalDouble();
		System.out.println(value);
	}
	
	public static void testEvaluateBooleanExpressionWithAliasVariables() {
		String expr = "{a+b*5/c^4>2, a=5, b = 1, c=2}";
		
		ExprEvaluator util = new ExprEvaluator();
		IExpr result = util.eval(expr);
		System.out.println(result);
//		IExpr expr = util.evaluate("x^2+y+a*x+b*y+c");
		
		Boolean value = Boolean.parseBoolean(result.getAt(1).toString());
		System.out.println(value);
	}
	
	public static void main(String[] args) {
		testValidateExpressionString();
//		testEvaluateNumericExpressionWithAliasVariables();
//		testEvaluateBooleanExpressionWithAliasVariables();
	}
}
