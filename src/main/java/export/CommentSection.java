package export;

import java.util.LinkedHashMap;


/**
 * contains the full set of information to generate comment section of an exported file by a {@link HasIDRelationTableContentExporter}
 * @author tanxu
 * 
 */
public class CommentSection {
	public static final String COMMENT_SPECIFIER = "#";
	public static final String CONTEXT = "VISFRAME"; //version?
	/////////////////////////
	private final String projectName;
	private final String dataSource;
	private final LinkedHashMap<String, Boolean> sortedColumnNameSortedWithASCMap;
	private final boolean allRowsIncluded;
	private final int totalRowNum;
	private final int startRowIndex;
	private final int endRowIndex;
	
	
	/**
	 * 
	 * @param projectName
	 * @param dataSource
	 * @param sortedColumnNameSortedWithASCMap the map for columns in ORDER BY clause; cannot be null; if empty, the original order is used;
	 * @param allRowsIncluded
	 * @param totalRowNum
	 * @param startRowIndex
	 * @param endRowIndex
	 */
	public CommentSection(
			String projectName,
			String dataSource,
			LinkedHashMap<String, Boolean> sortedColumnNameSortedWithASCMap,
			boolean allRowsIncluded,
			int totalRowNum,
			int startRowIndex,
			int endRowIndex
			){
		if(sortedColumnNameSortedWithASCMap==null)
			throw new IllegalArgumentException("given columnNameASCSortedMap cannot be null!");
		
		this.projectName = projectName;
		this.dataSource = dataSource;
		this.sortedColumnNameSortedWithASCMap = sortedColumnNameSortedWithASCMap;
		this.allRowsIncluded = allRowsIncluded;
		this.totalRowNum = totalRowNum;
		this.startRowIndex = startRowIndex;
		this.endRowIndex = endRowIndex;
	}
	
	/**
	 * build and return string
	 * @return
	 */
	String generateString() {
		StringBuilder sb = new StringBuilder();
		sb.append(COMMENT_SPECIFIER).append(CONTEXT).append("\n");
		sb.append(COMMENT_SPECIFIER).append("PROJECT_NAME=").append(this.projectName)
		.append(";").append("DATA_SOURCE=").append(this.dataSource).append("\n");
		sb.append(COMMENT_SPECIFIER).append("SORTED_COLUMN_AND_BY_ASC=").append(this.sortedColumnNameSortedWithASCMap.toString()).append("\n");
		sb.append(COMMENT_SPECIFIER).append("ALL_ROWS_INCLUDED=").append(this.allRowsIncluded).append("\n");
		sb.append(COMMENT_SPECIFIER).append("TOTAL_ROW_NUM=").append(this.totalRowNum)
		.append(";").append("START_ROW_INDEX=").append(this.startRowIndex)
		.append(";").append("END_ROW_INDEX=").append(this.endRowIndex).append("\n");
		
		return sb.toString();
	}
	
	
}
