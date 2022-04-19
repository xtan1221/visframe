package basic.project;

import context.project.VisProjectDBContext;

/**
 * base class for building an entity within a host VisProjectDBContext;
 * @author tanxu
 *
 * @param <T>
 */
public interface VisProjectBuilder<T> {
//	private final VisProjectDBContext hostVisProject;
//	
//	/**
//	 * 
//	 * @param hostVisProject
//	 */
//	protected VisProjectBuilder(VisProjectDBContext hostVisProject){
//		this.hostVisProject = hostVisProject;
//	}
	
	
	
	
	VisProjectDBContext getHostVisProjectDBContext();

	
	/**
	 * build and return the entity with the information contained in the builder
	 * @return
	 * @throws Exception 
	 */
	T build() throws Exception;


}
