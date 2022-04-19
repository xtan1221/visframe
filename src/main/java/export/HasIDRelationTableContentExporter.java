package export;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import context.project.rdb.VisProjectRDBConstants;

/**
 * 
 * @author tanxu
 *
 */
public class HasIDRelationTableContentExporter {
	public static final String COLUMN_DELIMITER = "\t";
	public static final String OUTPUT_FILE_FORMAT = "TSV";
	
	/////////////////////////////////
	/**
	 * contains the full set of rows to be exported;
	 */
	private final ResultSet resultSet; 
	
	/**
	 * must be consistent with the {@link #resultSet}
	 * note that the first column (index = 1) in {@link #resultSet} is RUID column, which is included in this list;
	 */
	private final List<String> nonRUIDHeaderColumnNameList;
	
	private final boolean toAddInforSection; //CommentSection
	private final CommentSection commentSection;
	
	private final boolean toAddHeaderLine; //column names
	private final boolean toIncludeRUIDColumn;
	/**
	 * full path of output file including the parent folder and the full file name
	 */
	private final Path outputFilePath;
	
	/////////////////////////////////
	
	/**
	 * 
	 * @param resultSet
	 * @param nonRUIDHeaderColumnNameList
	 * @param toAddInforSection
	 * @param commentSection
	 * @param toAddHeaderLine
	 * @param toIncludeRUIDColumn
	 * @param outputFilePath
	 */
	public HasIDRelationTableContentExporter(
			ResultSet resultSet,
			List<String> nonRUIDHeaderColumnNameList,
			boolean toAddInforSection,
			CommentSection commentSection,
			boolean toAddHeaderLine,
			boolean toIncludeRUIDColumn,
			Path outputFilePath
			){
		/////////////////////
		//
		
		this.resultSet = resultSet;
		this.nonRUIDHeaderColumnNameList = nonRUIDHeaderColumnNameList;
		this.toAddInforSection = toAddInforSection;
		this.commentSection = commentSection;
		this.toAddHeaderLine = toAddHeaderLine;
		this.toIncludeRUIDColumn = toIncludeRUIDColumn;
		this.outputFilePath = outputFilePath;
	}
	
	
	/**
	 * 
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public void run() throws IOException, SQLException {
		FileOutputStream fos = new FileOutputStream(this.outputFilePath.toFile());
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		//first write the infor section
		if(this.toAddInforSection)
			bw.write(this.commentSection.generateString());
		
		//write header line
		if(this.toAddHeaderLine)
			bw.write(this.buildHeaderLine());
		
		//write rows
		StringBuilder lineSB;
		boolean firstColAdded;
		while(this.resultSet.next()) {
			lineSB = new StringBuilder();
			firstColAdded = false;
			///
			if(this.toIncludeRUIDColumn) {
				lineSB.append(this.resultSet.getString(1));
				firstColAdded = true;
			}
			///
			for(int i=0;i<this.nonRUIDHeaderColumnNameList.size();i++) {
				if(firstColAdded) {
					lineSB.append("\t");
				}else {
					firstColAdded = true;
				}
				
				lineSB.append(this.resultSet.getString(i+2));
			}
			///
			bw.write(lineSB.toString());
			bw.newLine();
		}
		
		//close
		this.resultSet.close();
		bw.close();
		fos.close();
	}
	
	/**
	 * 
	 * @return
	 */
	private String buildHeaderLine() {
		StringBuilder sb = new StringBuilder();
		boolean firstAdded = false;
		
		sb.append(CommentSection.COMMENT_SPECIFIER);
		//////////
		if(this.toIncludeRUIDColumn) {
			sb.append(VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE);
			firstAdded = true;
		}
		
		///
		for(String colName:this.nonRUIDHeaderColumnNameList) {
			if(firstAdded) {
				sb.append("\t");
			}else {
				firstAdded = true;
			}
			sb.append(colName);
		}
		sb.append("\n");
		
		return sb.toString();
	}
	
	
}
