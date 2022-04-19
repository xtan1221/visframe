package context.project.rdb.initialize;

import context.project.VisProjectDBFeatures;

public abstract class AbstractVisProjectDBInitializer implements VisProjectDBInitializer {
	private final VisProjectDBFeatures projectDBFeatures;
	
	/**
	 * constructor
	 * @param projectDBConnection
	 */
	AbstractVisProjectDBInitializer(VisProjectDBFeatures projectDBFeatures){
		this.projectDBFeatures = projectDBFeatures;
	}
	
	
	public VisProjectDBFeatures getVisProjectDBFeatures() {
		return this.projectDBFeatures;
	}

}
