/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package hefty.frame.analysis;

import Data.Project;
import java.io.File;
import lib.FileOperations;
import lib.Logger;
import lib.Statics;

/**
 *
 * @author SHOT(by)GUN
 */

// How about someone renames this class to something more ... appropriate?
public class GodObject {
    
    String logFilePath = FileOperations.getWorkingDirectory() + File.separator + "HeftyFrameAnalysis.log";
    
    Logger logger;
    
    public GodObject() throws Exception {
        
        logger = new Logger(FileOperations.getOrCreateFile(logFilePath));
        logger.start();
        
    }
    
    public boolean shutdown() {
        Logger.log(Statics.applicationName, "shutdown requested");
        logger.stop();
        return true;
    }
    
    public void test() {
        try {
            Project project = new Project("D:\\Videot\\Stream\\filu (02).mp4");
            
        } catch (Exception ex) {
            Logger.log("GodObject", "error in test()", ex);
        }
    }
    
}
