/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package hefty;

import Data.BarGraph;
import Data.Project;
import java.io.File;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lib.DataDumper;
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
public class HeftyApplication {
    
    public HeftyApplication() {
        
        try {
            Statics.logger = new Logger(FileOperations.getOrCreateFile(Statics.logFilePath));
            Statics.logger.start();
            
            Statics.jobList = new JobList();
            
            Statics.updateThread = new UpdateThread();
            Statics.updateThread.start();
            
            
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
            final Project project = new Project(importedFile, tab);
            
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
            
        } catch (Exception ex) {
            Logger.log(HeftyApplication.class.getSimpleName(), "Error importing file", ex);
        }
    }
    
    public void dumpFrameData() {
        try {
            
            ConcurrentLinkedQueue dump = new ConcurrentLinkedQueue();
            DataDumper dumper = new DataDumper(dump);
            dumper.start();

            Logger.log(HeftyApplication.class.getSimpleName(), "dumpFrameData start");
            
            dump.add("++++ Dump Frame Data ++++ dump start");
            TabPane tabPane = Statics.mainGuiController.getRootTabPane();
            Project project;
            int i;
            for(Tab tab : tabPane.getTabs()) {
                // For each tab
                if(tab.getUserData() != null) {
                    // For each tab containing a project
                    project = (Project) tab.getUserData();

                    dump.add("++++++++ Project name: " + project.projectName);

                    for(i = 0; i < project.frames.size(); i++) {
                        dump.add("++++ Frame number: " + i);
                        for(Entry<String, String> row : project.frames.get(i).frameData.entrySet()) {
                            dump.add(row.getKey() + " = " + row.getValue());
                        }
                    }
                }
            }
            dump.add("++++ Dump Frame Data ++++ dump end");
            
            Logger.log(HeftyApplication.class.getSimpleName(), "dumpFrameData end");
        } catch (Exception ex) {
            Logger.log(HeftyApplication.class.getSimpleName(), "dumpFrameData error", ex);
        }
    }
}
