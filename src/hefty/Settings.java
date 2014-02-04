/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package hefty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import lib.FileOperations;
import static lib.FileOperations.getOrCreateFile;
import static lib.FileOperations.getWorkingDirectory;
import lib.Logger;

/**
 *
 * @author SHOT(by)GUN
 */
public class Settings {
    
    private final Properties defaultSettings = new Properties();
    private final Properties currentSettings;
    
    public Settings() {
        
        // Set default values
        defaultSettings.setProperty("ffprobePath", "");
        defaultSettings.setProperty("importAllData", "false");
        
        // Create current settings
        currentSettings = new Properties(defaultSettings);
        
        // load settings from file
        load();
    }
    
    public Properties getSettings() {
        return currentSettings;
    }
    
    public void save() {
        try {
            File outFile = getOrCreateFile(getWorkingDirectory() + File.separator + "settings.properties");
            OutputStream out = new FileOutputStream(outFile);
            currentSettings.store(out, "saving properties");
        } catch (Exception ex) {
            Logger.log(FileOperations.class.getSimpleName(), "error saving properties", ex);
        }
    }
    
    private void load() {
        try {
            File inFile = getOrCreateFile(getWorkingDirectory() + File.separator + "settings.properties");
            InputStream in = new FileInputStream(inFile);
            currentSettings.load(in);
        } catch (Exception ex) {
            Logger.log(FileOperations.class.getSimpleName(), "error loading properties", ex);
        }
    }
    
    
}
