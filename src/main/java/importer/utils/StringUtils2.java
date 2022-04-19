package importer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basic.SimpleName;



public class StringUtils2 {
	
	/**
	 * 1. cannot be empty string;
	 * 2. can only contain alphabetic letter, digit letter, and underscore
	 * 3. must start with alphabetic letter
	 * 4. case insensitive
	 * 5. has a maximal length
	 * @param original
	 * @return
	 */
	public static String transformStringToVfNameString(String original) {
		String ret = original.replaceAll("[^\\w\\_]", "_"); //\\w is word character: [a-zA-Z_0-9]
		
		if(ret.startsWith("_")) {
			Pattern p = Pattern.compile("\\_+(.*)");
//			Pattern p = Pattern.compile("_+([^\\_]*)");
			
			
			Matcher m = p.matcher(ret);
//			boolean found = m.find();
			boolean matches = m.matches(); //attempt to match
//			String m0 = m.group(0);
			
			ret = m.group(1);
		}
		
		//testing
		SimpleName sn = new SimpleName(ret);
		
		return ret;
	}
}
