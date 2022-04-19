package fileformat.record.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

import importer.utils.StringUtils;

public class StringMarkerUtils {
	
	/**
	 * check and return whether the given StringMarker is a new line character;
	 * 
	 * TODO
	 * 
	 * currently only allow '\n+'
	 * 
	 * @param stringValue
	 * @return
	 */
	public static boolean isNewLineCharacter(StringMarker stringMarker) {
//		System.out.print("Stringvalue:"+stringValue);
//		System.out.print("new line:"+StringUtils.NEW_LINE_STRING);
		
		String s = stringMarker.getStringValue();
		
//		String s = StringEscapeUtils.unescapeJava(stringMarker.getStringValue());
//		boolean result = s.equals(StringUtils.NEW_LINE_STRING);
////			return StringEscapeUtils.unescapeJava(stringMarker.getStringValue()).equals(StringUtils.NEW_LINE_STRING);
//		return result;
		Pattern p = Pattern.compile("\n");
		Matcher m = p.matcher(stringMarker.getStringValue());
//		return m.matches();
		
		return stringMarker.getStringValue().equals("\\n+") || stringMarker.getStringValue().equals("\\n"); //!!!
	}
	
	/**
	 * check whether the s2 StringMarker could be viewed as s1 StringMarker;
	 * 
	 * this is a non-trivial problem!!!; 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static boolean s1CoversS2(StringMarker s1, StringMarker s2) {
		//TODO
		
		
		
		
		return true;
	}
	
	
	
	/**
	 * check if the given list of StringMarker has following properties:
	 * 1. when split with a StringMarker with index i, all StringMarker with index > i will not be included;
	 * 
	 * 
	 * @param listOfMarker
	 * @return
	 */
	public static boolean validateAll(List<StringMarker> listOfMarker) {
		for(int i=0;i<listOfMarker.size();i++) {
			for(int j=i+1;j<listOfMarker.size();j++) {
				if(s1CoversS2(listOfMarker.get(i),listOfMarker.get(j))) {
					System.out.println(listOfMarker.get(i).getStringValue()+">"+listOfMarker.get(j).getStringValue());
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * check if the given data string contains the given StringMarker or not;
	 * @param dataString
	 * @param marker
	 * @return
	 */
	public static boolean contains(String dataString, StringMarker marker) {
		Pattern p;
		
		if(!marker.isCaseSensitive()) {
			p = Pattern.compile(marker.getStringValue(),Pattern.CASE_INSENSITIVE);
		}else {
			p = Pattern.compile(marker.getStringValue());
		}
		
		Matcher m = p.matcher(dataString);
		
		return m.find();
	}
	
	/**
	 * return a string by removing all trailing string of the delimiter PlainStringMarker from the given string;
	 * @param dataString
	 * @param delimiter
	 * @param includingDelimiter whether to remove the delimiter string or not;
	 * @return
	 */
	public static String removeTrailing(String dataString, PlainStringMarker delimiter, boolean includingDelimiter) {
		if(!contains(dataString, delimiter)) {
			return dataString;
		}
		
		if(!delimiter.isCaseSensitive()) {//case insensitive, transform to same case first, note that this is only applicable for PlainStringMarker!!!!!
//			return dataString.toUpperCase().split(delimiter.getStringValue().toUpperCase())[0];
			return split(dataString.toUpperCase(), new PlainStringMarker(delimiter.getStringValue().toUpperCase(),true), true, false).get(0);
		}else {
			return split(dataString, delimiter, true, false).get(0);
		}
	}
	
	/**
	 * split the given data string by the given StringMarker;
	 * 
	 * {@link String#split(String)} 
	 * @param dataString
	 * @param marker
	 * @return
	 */
	public static List<String> split(String dataString, StringMarker marker, boolean toKeepFirstEmpty, boolean toKeepLastEmpty) {
		List<String> splits;
		
		if(marker instanceof PlainStringMarker) {
			if(marker.isCaseSensitive()) {
				splits = StringUtils.splitS2ByS1AsExpected(marker.getStringValue(), dataString);
			}else {//case insensitive
				splits = StringUtils.splitS2ByS1AsExpected(marker.getStringValue().toLowerCase(), dataString.toLowerCase());
			}
			
		}else {
			
			splits = StringUtils.splitS2ByS1AsExpected(marker.getStringValue(), dataString);
		}
		
		
		if(!toKeepFirstEmpty && splits.get(0).isEmpty()) {
			splits.remove(0);
		}
		
		if(!toKeepLastEmpty && splits.get(splits.size()-1).isEmpty()) {
			splits.remove(splits.size()-1);
		}
		
		return splits;
	}
	
	
	/**
	 * 
	 * @param dataString
	 * @param delimiter
	 * @param n
	 * @return
	 */
	public static List<String> splitByFirstN(String dataString, StringMarker delimiter, int n) {
		if(!contains(dataString, delimiter)) {
			throw new IllegalArgumentException("");
		}
		
		String delimiterString;
		
		if(delimiter instanceof PlainStringMarker) {
			if(delimiter.isCaseSensitive()) {
				delimiterString = delimiter.getStringValue();
			}else {
				delimiterString = delimiter.getStringValue().toUpperCase();
				dataString = dataString.toUpperCase();
			}
		}else {
//			RegexStringMarker rsm = (RegexStringMarker)delimiter;
			delimiterString = delimiter.getStringValue();
		}
		
		List<String> splits = StringUtils.splitByFirstNDelimiters(delimiterString, dataString, n);
	
		
		return splits;
		
		
	}
	
	public static boolean endsWith(String dataString, StringMarker marker) {
		return dataString.endsWith(marker.getStringValue());
	}
	
	public static boolean startsWith(String dataString, StringMarker marker) {
		return dataString.startsWith(marker.getStringValue());
	}
	
	
	/**
	 * 
	 * @param dataString
	 * @param delimiterList
	 * @param dataStringStartingWithFirstDelimiter
	 * @return
	 */
	public static List<String> split(String dataString, List<StringMarker> delimiterList, boolean dataStringStartingWithFirstDelimiter){
		//TODO
		
		
		
		return null;
	}
}
