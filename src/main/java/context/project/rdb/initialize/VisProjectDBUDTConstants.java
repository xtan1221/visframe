package context.project.rdb.initialize;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import basic.lookup.PrimaryKeyID;
import context.project.process.logtable.StatusType;
import context.project.process.logtable.VfIDCollection;
import context.project.rdb.VisProjectRDBConstants;
import context.scheme.VisScheme;
import context.scheme.appliedarchive.VisSchemeAppliedArchive;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstance;
import fileformat.FileFormat;
import function.composition.CompositionFunction;
import function.group.CompositionFunctionGroup;
import function.variable.independent.IndependentFreeInputVariableType;
import importer.DataImporter;
import metadata.Metadata;
import operation.Operation;
import rdb.sqltype.UDTDataType;
import visinstance.VisInstance;
import visinstance.run.VisInstanceRun;
import visinstance.run.calculation.function.composition.CFTargetValueTableRun;
import visinstance.run.layoutconfiguration.VisInstanceRunLayoutConfiguration;

/**
 * UDT types used in rdb of visframe project;
 * 
 * @author tanxu
 *
 */
public class VisProjectDBUDTConstants {
	/**
	 * schema name string for visframe UDT, which is the MANAGEMENT SCHEMA;
	 */
	public static final String UDT_SCHEMA_NAME = VisProjectRDBConstants.MANAGEMENT_SCHEMA_NAME.getStringValue();
	
	
	static final String vfPrefix = "VF_";
	
	/**
	 * the set of UDTs that need to be created in the MANAGEMENT schema of the DB of any VisProjectDBContext;
	 * 
	 * note that only those that are to be used as the UDT of a table column need to be put here;
	 * 
	 * all must be of {@link Serializable}
	 */
	public static final Map<Class<? extends Serializable>, String> visframeUDTAliasNameMap;
	
	static {
		//VisframeUDT management
		visframeUDTAliasNameMap = new HashMap<>();
		visframeUDTAliasNameMap.put(FileFormat.class,vfPrefix.concat(FileFormat.class.getSimpleName()));
		visframeUDTAliasNameMap.put(DataImporter.class, vfPrefix.concat(DataImporter.class.getSimpleName()));
		visframeUDTAliasNameMap.put(Operation.class, vfPrefix.concat(Operation.class.getSimpleName()));
		visframeUDTAliasNameMap.put(Metadata.class, vfPrefix.concat(Metadata.class.getSimpleName()));//METADATA is used by a UDT defined by apache derby
		
		visframeUDTAliasNameMap.put(CompositionFunctionGroup.class, vfPrefix.concat(CompositionFunctionGroup.class.getSimpleName()));
		visframeUDTAliasNameMap.put(CompositionFunction.class, vfPrefix.concat(CompositionFunction.class.getSimpleName()));
		visframeUDTAliasNameMap.put(IndependentFreeInputVariableType.class, vfPrefix.concat(IndependentFreeInputVariableType.class.getSimpleName()));
		
		visframeUDTAliasNameMap.put(VisScheme.class, vfPrefix.concat(VisScheme.class.getSimpleName()));
		visframeUDTAliasNameMap.put(VisSchemeAppliedArchive.class, vfPrefix.concat(VisSchemeAppliedArchive.class.getSimpleName()));
		visframeUDTAliasNameMap.put(VisSchemeAppliedArchiveReproducedAndInsertedInstance.class, vfPrefix.concat(VisSchemeAppliedArchiveReproducedAndInsertedInstance.class.getSimpleName()));
		
		visframeUDTAliasNameMap.put(VisInstance.class, vfPrefix.concat(VisInstance.class.getSimpleName()));
		visframeUDTAliasNameMap.put(VisInstanceRun.class, vfPrefix.concat(VisInstanceRun.class.getSimpleName()));
		visframeUDTAliasNameMap.put(CFTargetValueTableRun.class, vfPrefix.concat(CFTargetValueTableRun.class.getSimpleName()));
		visframeUDTAliasNameMap.put(VisInstanceRunLayoutConfiguration.class, vfPrefix.concat(VisInstanceRunLayoutConfiguration.class.getSimpleName()));
		
		//insertion process id column in management table and process id in process log table
		visframeUDTAliasNameMap.put(PrimaryKeyID.class, vfPrefix.concat(PrimaryKeyID.class.getSimpleName()));
		
		
		//note that specific ID subtype are not used in process log table columns!
		//thus do not need to add them
	
		//helper UDTs used in VisframeUDT management table and/or PROCESS LOG table
		visframeUDTAliasNameMap.put(VfIDCollection.class, vfPrefix.concat(VfIDCollection.class.getSimpleName()));
		visframeUDTAliasNameMap.put(StatusType.class, vfPrefix.concat(StatusType.class.getSimpleName()));
	}
	
	
	/**
	 * return the UDTDataType for the given clazz;
	 * 
	 * if the given clazz is not found in the {@link visframeUDTAliasNameMap}, throw NullPointerException;
	 * 
	 * @param clazz
	 * @return
	 */
	public static UDTDataType getSQLDataType(Class<? extends Serializable> clazz) {
		return new UDTDataType(VisProjectRDBConstants.MANAGEMENT_SCHEMA_NAME, visframeUDTAliasNameMap.get(clazz));
	}
	
}
