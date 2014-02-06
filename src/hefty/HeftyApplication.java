/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package hefty;

import Data.Project;
import java.io.File;
import javafx.scene.control.Tab;
import lib.FileOperations;
import lib.JobList;
import lib.Logger;
import lib.Statics;
import lib.UpdateThread;

/**
 *
 * @author SHOT(by)GUN
 */

// How about someone renames this class to something more ... appropriate?
public final class HeftyApplication {
    
    public HeftyApplication() {
        
        try {
            Statics.logger = new Logger(FileOperations.getOrCreateFile(Statics.logFilePath));
            Statics.logger.start();
            
            Statics.jobList = new JobList();
            
            Statics.updateThread = new UpdateThread();
            Statics.updateThread.start();
            
            Statics.settings = new Settings();
            
        } catch (Exception ex) {
            String errorMessage = "Error initializing " 
                    + HeftyApplication.class.getSimpleName() 
                    + ", nothing should be working";
            System.out.println(errorMessage);
            Statics.mainGuiController.setLogText(errorMessage);
            ex.printStackTrace();
        }
        
    }
    
    public boolean shutdown() {
        Logger.log(Statics.applicationName, "shutdown requested");
        Statics.logger.stop();
        Statics.updateThread.stop();
        return true;
    }
    
    public void createNewProject(File importedFile) {
        try {
            Logger.log(Statics.applicationName, "Importing file " + importedFile.getAbsolutePath());
            
            Tab tab = new Tab(importedFile.getName());
            Project project = new Project(importedFile, tab);
            
            Statics.mainGuiController.getRootTabPane().getTabs().add(tab);
            tab.setUserData(project);
            
            if(tab.getTabPane().getSelectionModel().getSelectedItem().getUserData() == null)
                tab.getTabPane().getSelectionModel().select(tab);
            
            // TODO add option to import part of the file
            project.startReadingFrames();
            
        } catch (Exception ex) {
            Logger.log(HeftyApplication.class.getSimpleName(), "Error importing file", ex);
        }
    }
    
}
