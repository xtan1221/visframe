package exception;


/**
 * thrown if a cycle is detected in a dependency graph
 * @author tanxu
 *
 */
public class CycleFoundInDependencyGraphException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6129018912215086828L;

	public CycleFoundInDependencyGraphException(String msg){
		super(msg);
	}
}
