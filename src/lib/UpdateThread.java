/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package lib;

import Data.Project;
import java.io.File;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Platform;
import javafx.scene.control.Tab;

/**
 *
 * @author SHOT(by)GUN
 */
public class UpdateThread extends RootThread {
    
    
    private boolean stopRequested = false;
    
    private ReentrantLock lock = new ReentrantLock();
    
    
    public UpdateThread() {
        super(UpdateThread.class.getSimpleName());
    }

    @Override
    public void run() {
        while(keepRunning) {
            try {
                
                Thread.sleep(20);
                
                if(stopRequested) {
                    for(Tab tab : Statics.mainGuiController.getRootTabPane().getTabs()) {
                        if(tab.getUserData() != null) {
                            Project project = (Project) tab.getUserData();
                            project.closeProject();
                        }
                    }
                    
                    super.stop();
                    
                } else {
                    
                    
                    // Update GUI
                    Platform.runLater(updateGUI);
                    
                }
                
                
            } catch (Exception ex) {
                Logger.log(UpdateThread.class.getSimpleName(), "error while running", ex);
            }
        }
    }
    
    
    @Override
    public void stop() {
        stopRequested = true;
    }
    
    public Runnable updateGUI = new Runnable() {
        
        // Runtime variables
        Project project;
        
        @Override
        public void run() {
            
            if(!lock.tryLock()) {
                Logger.log("Platform.runLater.updateGUI", "lock hit!");
                return;
            }
            
            try {
                
                // Update graphics
                for(Tab tab : Statics.mainGuiController.getRootTabPane().getTabs()) {
                    if(tab.getUserData() != null) {
                        project = (Project) tab.getUserData();
                        project.barGraph.updateSlider();
                        project.barGraph.draw();
                    }
                }
                
                // Add new project
                File file;
                if((file = Statics.importFileQueue.poll()) != null)
                    Statics.application.createNewProject(file);

                Statics.jobList.printJobs.run();
                
            } catch (Exception ex) {
                Logger.log("Platform.runlater.updateGUI", "error while running", ex);
            } finally {
                lock.unlock();
            }
        }
    };
    
    
}
