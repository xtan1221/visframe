package basic.lookup.project;

import basic.lookup.ID;
import context.project.VisProjectDBContext;
import basic.lookup.HasID;

/**
 * 
 * @author tanxu
 *
 * @param <T>
 */
public abstract class VisProjectHasIDTypeManagerBase <T extends HasID,I extends ID<T>> implements VisProjectHasIDTypeManager<T,I>{
	private final VisProjectDBContext visProjectDBContext;
	
	private final Class<T> managedType;
	private final Class<I> IDType;
	
	/**
	 * constructor
	 * @param hostVisProjectDBConnection
	 */
	public VisProjectHasIDTypeManagerBase(
			VisProjectDBContext visProjectDBContext,
			Class<T> managedType,Class<I> IDType
			) {
		
		this.visProjectDBContext = visProjectDBContext;
		this.managedType = managedType;
		this.IDType = IDType;
	}
	
	@Override
	public VisProjectDBContext getHostVisProjectDBContext() {
		return this.visProjectDBContext;
	}
	
	
	@Override
	public Class<T> getManagedType(){
		return this.managedType;
	}
	
	/**
	 * return the class of ID type
	 */
	@Override
	public Class<I> getIDType(){
		return this.IDType;
	}
}
