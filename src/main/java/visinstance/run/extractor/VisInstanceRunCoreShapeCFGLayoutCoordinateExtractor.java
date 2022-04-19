package visinstance.run.extractor;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import context.project.VisProjectDBContext;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;
import function.group.ShapeCFG;
import javafx.geometry.Point2D;
import visinstance.run.VisInstanceRun;
import visinstance.run.calculation.function.composition.CFTargetValueTableRun;


/**
 * extractor of all calculated layout coordinate from corresponding CFTarget value table for each of the core ShapeCFG of a VisInstanceRun;
 * 
 * facilitate generating previous layout of a VisInstanceRun;
 * 
 * @author tanxu
 * 
 */
public final class VisInstanceRunCoreShapeCFGLayoutCoordinateExtractor 
	extends VisInstanceRunExtractorBase<Point2D, CoreShapeCFGCalculatedLayoutCoordinateExtractor>{
	
	/**
	 * 
	 * @param hostVisProjectDBContext
	 * @param visInstanceRun
	 * @throws SQLException
	 */
	public VisInstanceRunCoreShapeCFGLayoutCoordinateExtractor(VisProjectDBContext hostVisProjectDBContext,
			VisInstanceRun visInstanceRun) throws SQLException {
		super(hostVisProjectDBContext, visInstanceRun);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public CoreShapeCFGCalculatedLayoutCoordinateExtractor nextCoreShapeCFGExtractor() throws SQLException {
		if(!this.unprocessedCoreShapeCFGIDSet.isEmpty()) {
			CompositionFunctionGroupID coreShapeCFGID = this.unprocessedCoreShapeCFGIDSet.iterator().next();
			this.unprocessedCoreShapeCFGIDSet.remove(coreShapeCFGID);
			
			ShapeCFG coreShapeCFG = (ShapeCFG)this.getHostVisProjectDBContext().getCompositionFunctionGroupLookup().lookup(coreShapeCFGID);
			
			//
			Map<CompositionFunction, CFTargetValueTableRun> compostionFunctionCFTargetValueTableRunMap = new HashMap<>();
			
			for(CompositionFunctionID cfID:this.getVisInstanceRunPreprocessor().getCfIDIndependetFIVTypeStringValueMapMap().keySet()){
				if(cfID.getHostCompositionFunctionGroupID().equals(coreShapeCFG.getID())) {
					CompositionFunction cf = this.getHostVisProjectDBContext().getCompositionFunctionLookup().lookup(cfID);
					
					CFTargetValueTableRun run = 
							this.getHostVisProjectDBContext().getHasIDTypeManagerController().getCFTargetValueTableRunManager().lookupRun(
									cfID, this.getVisInstanceRunPreprocessor().getCfIDIndependetFIVTypeStringValueMapMap().get(cfID));
					//
					compostionFunctionCFTargetValueTableRunMap.put(cf, run);
				}
			}
			
			return new CoreShapeCFGCalculatedLayoutCoordinateExtractor(this.getHostVisProjectDBContext(), coreShapeCFG, compostionFunctionCFTargetValueTableRunMap);
		}else {
			return null;
		}
	}
	
}
