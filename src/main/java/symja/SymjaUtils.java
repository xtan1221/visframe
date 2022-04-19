package symja;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.parser.ExprParserFactory;

import rdb.sqltype.SQLDataType;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;


/**
 * Every expression in Symja is built upon the same principle: 
 * 		it consists of a head and an arbitrary number of children, unless it is an atom, 
 * 		i.e. it can not be subdivided any further. To put it another way: everything is a function call. 
 * 		Nested calculations are nested function calls;
 * 
 * ==============================
 * for source code and documentation, go to https://github.com/axkr/symja_android_library
 * 
 * for built in functions, see https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc
 * 
 * ==============================
 * visframe allowed symja expression variable naming convention (tested in visframe but may not be the same with the symja's own convention, which may be less strict)!:
 * 1. must start with alphabetical letter;
 * 2. can only contain alphabetical letter and digit; underscore is NOT allowed!
 * for example, "2r4s" is equivalent to 2*r4s; 'r4s2dww22' is a valid variable name
 * 
 * 
 * ===============================
 * symja variable name are case sensitive, thus 'A' and 'a' in expression 'a+4-A' are considered as two different variables!!!!!!!!!!!!!!!!!!
 * 
 * @author tanxu
 *
 */
public class SymjaUtils {
	/**
	 * must be re-initialized every time it is used;
	 * if it is initialized as static field, unexpected result may be generated!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	private static ExprEvaluator exprEvaluator;
	
	
	/**
	 * check and return if the given string is a valid symja expression;
	 * 
	 * note that org.matheclipse.parser.client.SyntaxError will be thrown!
	 * 
	 * thus use try-catch clause
	 * @param exprString
	 * @return
	 */
	public static void validateExpressionString(String exprString) {
		exprEvaluator = new ExprEvaluator();
		exprEvaluator.eval(exprString);
		
	}
	
	
	public static SQLDataType expressionDataType(String exprString) {
		if(firstLevelASTFunctionIsSimpleLogic(exprString)) {
			return SQLDataTypeFactory.booleanType();
		}
		//TODO
//		else if() {
//			
//		}
		return null;
	}
	
	
	
	/**
	 * extract and return the set of variable alias names from the given expression string;
	 * @param exprString
	 * @return
	 */
	public static Set<VfSymjaVariableName> extractVariableNameSet(String exprString) {
		Set<VfSymjaVariableName> ret = new LinkedHashSet<>();
		findOutAllAtomASTWithNon0Index(exprString).forEach((k,v)->{
			if(v) {
				ret.add(new VfSymjaVariableName(k));
			}
		});
		
		return ret;
	}
	
	
	/**
	 * evaluate and return the string value of the given expression string and the variable string values;
	 * 
	 * @param exprString
	 * @param variableAliasStringValueMap
	 * @return
	 */
	public static String evaluate(String exprString, Map<VfSymjaVariableName, String> variableAliasStringValueMap, Map<VfSymjaVariableName, VfDefinedPrimitiveSQLDataType> variableNameSQLDataTypeMap) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("{").append(exprString);
		
		variableAliasStringValueMap.forEach((k,v)->{
			sb.append(",").append(k).append("=");
			
			if(variableNameSQLDataTypeMap.get(k).isOfStringType()) {
				sb.append("\"").append(v).append("\""); //string type variable's value should be covered by double quote? 
			}else {
				sb.append(v);
			}
		});
		
		sb.append("}");
		
		//
		exprEvaluator = new ExprEvaluator();
		IExpr result = exprEvaluator.eval(sb.toString());
		
//		IExpr v = result.getAt(1);
//		String s = v.toString();
//		double d = result.getAt(1).evalDouble();
		
		return result.getAt(1).toString();
	}
	
	
	
	////////////////////////////
	/**
	 * 
	 * @param expression
	 * @return
	 */
	private static boolean isAtom(String expression){
        try {
            final ExprParser parser = new ExprParser(EvalEngine.get(), ExprParserFactory.RELAXED_STYLE_FACTORY, true);
            IExpr result = parser.parse(expression);
            return result.isAtom();

        } catch (org.matheclipse.parser.client.SyntaxError e) {
            System.out.println("Basic format error:" + e.getMessage());
            return false;
        }
    }
    
	/**
	 * abstract syntax tree
	 * see https://github.com/axkr/symja-parser
	 * 
	 * Test if this expression is an AST list, which contains a <b>header element</b> (i.e. the function name) at index
	 * position <code>0</code> and some optional <b>argument elements</b> at the index positions <code>1..n</code>.
	 * Therefore this expression is no <b>atomic expression</b>.
	 * @param expression
	 * @return
	 */
	protected static boolean isAst(String expression){
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
    private static boolean isSetValueExpression(String expression){
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
    private static boolean firstLevelASTFunctionIsSimpleLogic(String expression){
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
    private static boolean checkSyntaxOfExpressionString(String expression) {
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
    private static Map<String,Boolean> findOut0IndexASTItems(String expression) {
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
    private static Map<String, Boolean> findOutAllAtomASTWithNon0Index(String expression){
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
