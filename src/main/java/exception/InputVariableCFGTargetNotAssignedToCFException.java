package exception;


/**
 * a CFG target used as CFGTargetInputVariable but not assigned to any CF;
 * 
 * occur during the building of CFD graph;
 * 
 * 
 * @author tanxu
 *
 */
public class InputVariableCFGTargetNotAssignedToCFException extends RuntimeException{
	
	
	
	public InputVariableCFGTargetNotAssignedToCFException(String msg){
		super("CFGTargetInputVariable's contained target is not assigned to any CompositionFunction;".concat(msg));
	}
}
