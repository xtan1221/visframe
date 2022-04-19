package visinstance.run.statistics;


/**
 * contains the full statistics of the calculated core Shapes of a VisInstanceRun;
 * 
 * facilitate to analyze the VisInstanceRun and create VisInstanceRunLayouts;
 * 
 * each VisInstanceRun should have exactly one VisInstanceRunStatistics;
 * 
 * 
 * =========================================
 * VisInstanceRunStatistics should be generated when the first VisInstanceRunLayout of a VisInstanceRun is to be created;
 * 
 * once the VisInstanceRunStatistics is generated, it should be inserted into the VisInstanceRun management table thus can be re-used later if needed;
 * 
 * 1. for each core ShapeCFG, the range of layout region of all Shape instances;
 * 
 * 2. a full layout region for all core ShapeCFGs
 * 
 * 3. distribution of each core ShapeCFG on X and Y axis;
 * 		so that the distribution of any combination of core ShapeCFGs can be generated easily
 * 
 * 
 * @author tanxu
 * 
 */
public class VisInstanceRunStatistics {
	
	
}
