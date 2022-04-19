package visinstance.run.extractor;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import context.project.VisProjectDBContext;
import function.group.CompositionFunctionGroupID;
import visinstance.VisInstance;
import visinstance.run.VisInstanceRun;
import visinstance.run.calculation.VisInstanceRunPreprocessor;

/**
 * 
 * @author tanxu
 *
 * @param <T>
 * @param <E>
 */
public abstract class VisInstanceRunExtractorBase<T, E extends CoreShapeCFGExtractorBase<T>> {
	private final VisProjectDBContext hostVisProjectDBContext;
	
	private final VisInstanceRun visInstanceRun;
	
	//////////////////////////
	private VisInstance visInstance;
	private VisInstanceRunPreprocessor visInstanceRunPreprocessor;
	protected Set<CompositionFunctionGroupID> processedCoreShapeCFGIDSet;
	protected Set<CompositionFunctionGroupID> unprocessedCoreShapeCFGIDSet;
	
	/**
	 * 
	 * @param hostVisProjectDBContext
	 * @param visInstanceRun
	 * @throws SQLException
	 */
	protected VisInstanceRunExtractorBase(
		VisProjectDBContext hostVisProjectDBContext,
		VisInstanceRun visInstanceRun
		)throws SQLException{
		
		this.hostVisProjectDBContext = hostVisProjectDBContext;
		this.visInstanceRun = visInstanceRun;
		
		
		this.preprocess();
	}
	
	/**
	 * @return the hostVisProjectDBContext
	 */
	protected VisProjectDBContext getHostVisProjectDBContext() {
		return hostVisProjectDBContext;
	}

	/**
	 * @return the visInstanceRun
	 */
	protected VisInstanceRun getVisInstanceRun() {
		return visInstanceRun;
	}

	/**
	 * @return the visInstance
	 */
	protected VisInstance getVisInstance() {
		return visInstance;
	}

	/**
	 * @return the visInstanceRunPreprocessor
	 */
	protected VisInstanceRunPreprocessor getVisInstanceRunPreprocessor() {
		return visInstanceRunPreprocessor;
	}

	/**
	 * @return the processedCoreShapeCFGIDSet
	 */
	protected Set<CompositionFunctionGroupID> getProcessedCoreShapeCFGIDSet() {
		return processedCoreShapeCFGIDSet;
	}

	/**
	 * @return the unprocessedCoreShapeCFGIDSet
	 */
	protected Set<CompositionFunctionGroupID> getUnprocessedCoreShapeCFGIDSet() {
		return unprocessedCoreShapeCFGIDSet;
	}
	
	
	//////////////////////////
	private void preprocess() throws SQLException {
		this.visInstance = this.hostVisProjectDBContext.getHasIDTypeManagerController().getVisInstanceManager().lookup(this.visInstanceRun.getVisInstanceID());
		
		this.visInstanceRunPreprocessor = new VisInstanceRunPreprocessor(this.hostVisProjectDBContext, this.visInstanceRun);
		
		this.processedCoreShapeCFGIDSet = new LinkedHashSet<>();
		
		this.unprocessedCoreShapeCFGIDSet = new LinkedHashSet<>();
		
		this.unprocessedCoreShapeCFGIDSet.addAll(this.visInstance.getCoreShapeCFGIDSet());
		
	}
	
	
	/**
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public abstract E nextCoreShapeCFGExtractor() throws SQLException;
	
	
}
