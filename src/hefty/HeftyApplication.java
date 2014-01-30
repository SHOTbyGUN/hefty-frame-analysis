/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package hefty;

import Data.BarGraph;
import Data.Project;
import java.io.File;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
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
            final Project project = new Project(importedFile.getAbsolutePath());
            
            Tab tab = new Tab(importedFile.getName());
            Statics.mainGuiController.getRootTabPane().getTabs().add(tab);
            tab.setUserData(project);
            tab.getTabPane().getSelectionModel().select(tab);
            tab.setOnClosed(new EventHandler<Event>() {
                @Override
                public void handle(Event t) {
                    project.closeProject();
                }
            });
            // TODO add option to import part of the file
            project.startReadingFrames();
            
            BarGraph graph = new BarGraph(project, tab);
            
        } catch (Exception ex) {
            Logger.log(HeftyApplication.class.getSimpleName(), "Error importing file", ex);
        }
    }
}
