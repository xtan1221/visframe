package importer;

import context.project.VisProjectDBContext;

public abstract class AbstractFileParser implements FileParser {
	private final VisProjectDBContext visProject;
	
	/**
	 * constructor
	 * @param visProject cannot be null
	 */
	public AbstractFileParser(VisProjectDBContext visProject){
		//TODO validations
		
		this.visProject = visProject;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public VisProjectDBContext getVisProjectDBContext() {
		return this.visProject;
	}
	
}
