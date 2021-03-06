/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import hefty.HeftyApplication;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.commons.lang3.SystemUtils;

/**
 *
 * @author shotbygun
 */
public class FileOperations {
    
    public static String getWorkingDirectory() {
        if(SystemUtils.IS_OS_WINDOWS) {
            return System.getenv("APPDATA") + File.separator + Statics.applicationShortName;
        } else {
            // Linux / Unix / Mac?
            return System.getProperty("user.home") + File.separator 
                    + "." + Statics.applicationShortName;
        }
    }
    
    public static File getFile(String path) throws FileNotFoundException {
        File file = new File(path);
        if(!file.exists())
            throw new FileNotFoundException("Path: '" + path + "' not found");
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
}
