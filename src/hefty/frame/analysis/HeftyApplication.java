/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package hefty.frame.analysis;

import Data.Project;
import java.io.File;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lib.FileOperations;
import lib.JobList;
import lib.Logger;
import lib.Statics;

/**
 *
 * @author SHOT(by)GUN
 */

// How about someone renames this class to something more ... appropriate?
public class HeftyApplication {
    
    
    
    public HeftyApplication() {
        
        try {
            Statics.logger = new Logger(FileOperations.getOrCreateFile(Statics.logFilePath));
            Statics.logger.start();
            
            Statics.jobList = new JobList();
            Statics.jobList.start();
            
            
        } catch (Exception ex) {
            String errorMessage = "Error initializing GodObject, nothing should be working";
            System.out.println(errorMessage);
            Statics.mainGuiController.setLogText(errorMessage);
        }
        
    }
    
    public boolean shutdown() {
        Logger.log(Statics.applicationName, "shutdown requested");
        Statics.logger.stop();
        Statics.jobList.stop();
        return true;
    }
    
    public void createNewProject(File importedFile) {
        try {
            Logger.log(Statics.applicationName, "Importing file " + importedFile.getAbsolutePath());
            Tab tab = new Tab(importedFile.getName());
            Statics.mainGuiController.getRootTabPane().getTabs().add(tab);
            
            Project project = new Project(importedFile.getAbsolutePath());
            
            tab.setUserData(project);
            
            project.startReadingFrames(123, 456);
        } catch (Exception ex) {
            Logger.log(HeftyApplication.class.getSimpleName(), "Error importing file", ex);
        }
    }
}
