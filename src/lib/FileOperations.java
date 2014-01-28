/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import hefty.frame.analysis.GodObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author shotbygun
 */
public class FileOperations {
    
    public static String getWorkingDirectory() {
        if(isWindows()) {
            return System.getenv("APPDATA") + File.separator + Statics.applicationShortName;
        } else {
            // Linux / Unix / Mac?
            return System.getProperty("user.home") + File.separator 
                    + "." + Statics.applicationShortName;
        }
    }
    
    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
    
    public static File getFile(String path) throws Exception {
        File file = new File(path);
        if(!file.exists())
            throw new Exception("Path: '" + path + "' not found");
        return file;
    }
    
    public static File getOrCreateFile(String path) throws Exception {
        File file = new File(path);
        if(file.exists())
            return file;
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (Exception ex) {
            Logger.log(FileOperations.class.getSimpleName(), "Unable to create file or path to file: " + path, ex);
            return null;
        }
        return file;
    }
    
    public static File getOrCreateDirectory(String path) throws Exception {
        File directory = new File(path);
        if(directory.exists()) {
            // Check if path is really a directory
            if(!directory.isDirectory())
                throw new Exception("Given path '" + path + "' is not a directory");
            return directory;
        } else {
            // Create directory
            if(directory.mkdir())
                return directory;
            else
                throw new Exception("Unable to create directory to '" + path + "'");
        }
    }
    
    public static String[] readDictionary(File dictionary) throws Exception {
        ArrayList list = new ArrayList();
        BufferedReader br = new BufferedReader(new FileReader(dictionary));
        String line;
        while ((line = br.readLine()) != null) {
           list.add(line);
        }
        br.close();
        
        // Make array
        String[] output = new String[list.size()];
        list.toArray(output);
        
        return output;
    }
    
    public static String buildCommandString(String executable, String threadKeyFilePath) {
        return "\"" +executable + "\" enc -d -p -aes-256-cbc -a -in \"" + threadKeyFilePath + "\" -pass \"pass:";
    }
    
}
