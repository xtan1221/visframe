package context.project;

import java.nio.file.Path;

import basic.SimpleName;

/**
 * constants of VisProjectDBContext;
 * 
 * @author tanxu
 *
 */
public class VisProjectDBUtils {
	public static final String DB_DIR_NAME = "db";
	
	public static final String LOG_FILE_FULL_NAME = "LOG.vf";
	
	/**
	 * make and return the project directory path;
	 * @param projectParentDirPath
	 * @param projectName
	 * @return
	 */
	public static Path makeProjectDirPath(Path projectParentDirPath, SimpleName projectName) {
		return null;
	}
	
	
	/**
	 * make and return the project RDB directory path, which is a child folder of the project directory with folder name {@link DB_DIR_NAME};
	 * 
	 * @param projectParentDirPath
	 * @param projectName
	 * @return
	 */
	public static Path makeProjectRDBDirPath(Path projectParentDirPath, SimpleName projectName) {
		return null;
	}
	
	
	
	
}
