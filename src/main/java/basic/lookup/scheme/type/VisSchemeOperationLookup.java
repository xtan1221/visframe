package basic.lookup.scheme.type;

import java.util.Map;

import basic.lookup.scheme.VisSchemeLookup;
import operation.Operation;
import operation.OperationID;

public class VisSchemeOperationLookup implements VisSchemeLookup<Operation,OperationID>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6910118677860091582L;
	
	
	//////////////
	private final Map<OperationID, Operation> operationIDMap;
	
	/**
	 * constructor
	 * @param operationIDMap
	 */
	public VisSchemeOperationLookup(Map<OperationID, Operation> operationIDMap){
		this.operationIDMap = operationIDMap;
	}
	
	
	@Override
	public Map<OperationID, Operation> getMap() {
		return this.operationIDMap;
	}
	
}
