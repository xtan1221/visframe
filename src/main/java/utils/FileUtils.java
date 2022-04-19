package utils;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * utility methods regarding file system
 * @author tanxu
 *
 */
public class FileUtils {
	
	/**
	 * check whether the file of the given location exists or not
	 * @param filedirectoryString
	 * @return
	 */
	public static boolean doesFileExist(String filedirectoryString) {
		return Files.exists(Paths.get(filedirectoryString));
	}
}
