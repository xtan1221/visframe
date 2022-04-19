/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package importer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author tanxu
 */
public class FileIOUtils {
    
    /**
     * read a small plain text file as a single string with new line character replaced with System.lineSeparator if addLineSeparator is true
     * @param infile 
     * @param toAddLineSeparator 
     * @param toTrimLine 
     * @return  
     */
    public static String readFullPlainFileAsSingleString(File infile, boolean toAddLineSeparator, boolean toTrimLine){
        BufferedReader br;
        StringBuilder sb = new StringBuilder();
        try{
            br = new BufferedReader(new FileReader(infile));
            
            String line;
            
            while ((line = br.readLine() )!= null) {
                if(toTrimLine){
                    line = line.trim();
                }
                sb.append(line);
                if(toAddLineSeparator){
                    sb.append(StringUtils.NEW_LINE_STRING);
                }
                
            }
            
        } catch(IOException ie){
        }
        
        return sb.toString();
        
    }
    
    /**
     * check if the given file's extension is the same as the given extension string (case insensitive); 
     * for example, if ext = rff, then return true if file ends with .rff;
     * @param file
     * @param ext the file extension;
     * @return 
     */
    public static boolean fileExtensionEquals(File file, String ext){
        if(file.isFile()){
            String fileName = file.getName();
            
            String endingString = fileName.substring(fileName.length()-ext.length()-1, fileName.length());
//            System.out.println(endingString);
            if(endingString.equalsIgnoreCase(".".concat(ext))){
                return true;
            }else{
                return false;
            }
            
        }else{
            return false;
        }
    }
    
    
    
    /**
     * 
     * @param parentDirString path string of an existing directory
     * @param folderNameString
     * @return 
     */
    public static String makeURLStringOfNewFolderUnderParentDir(String parentDirString, String folderNameString){
        File parentDir = new File(parentDirString);
        return makeURLStringOfNewFolderUnderParentDir(parentDir,folderNameString);
    }
    /**
     * 
     * @param parentDir an existing directory
     * @param folderNameString name of a new folder, can not contain any file system separator string
     * @return 
     */
    public static String makeURLStringOfNewFolderUnderParentDir(File parentDir, String folderNameString){
        if(!parentDir.isDirectory()){
            throw new IllegalArgumentException("given parent directory is not an existing directory");
        }
        
        if(folderNameString.contains(File.separator)){
            throw new IllegalArgumentException("given folder name is invalid");
        }
        
        System.out.println(parentDir.getAbsolutePath().concat(File.separator).concat(folderNameString));
        return parentDir.getAbsolutePath().concat(File.separator).concat(folderNameString);
        
    }
    
    
    
    
    //
    public static void main(String[] args){
        String s = readFullPlainFileAsSingleString(new File("C:\\Users\\tanxu\\Desktop\\1.txt"),true, true);
        System.out.println("string from plain file:"+s);
//        System.out.println("is white space:"+StringUtils.isWhiteSpaces(s));
//        System.out.println(s.matches("\\s+"));
//        System.out.println("\t".matches("\t"));
//        
//        
//        String s1 = "\t";
//        System.out.println(s1);
//        System.out.println(StringUtils.isWhiteSpaces(s1));
//        System.out.println("string from plain file equals java \\t:"+s.equals(s1));
//        
//        String s3 = "12sssss34ss5s67,";
//        Pattern p = Pattern.compile(s);
//        Matcher m = p.matcher(s1);
//        StringUtils.printStringArray(p.split(s3));
//        StringUtils.printStringArray(s3.split(s));
        
        
//        System.out.println("find:"+m.find());
//        
//        
//        String s2 = "s\t1\t33333\ts\t   ";
////        String s3 = "12sssss34ss5s67,";
//        StringUtils.printStringArray(s2.split(s));
        
//        readFullPlainFileAsString();


        File f = new File("C:\\Users\\tanxu\\Desktop\\tree visualization tool\\2019\\slides\\data management and database\\rff\\GFF3.rff");
        System.out.println(fileExtensionEquals(f,"rff"));
    }
    
}
