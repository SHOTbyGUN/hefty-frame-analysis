/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.application.Platform;
import javafx.scene.control.ListView;

/**
 *
 * @author SHOT(by)GUN
 */
public class JobList extends TickThread {
    
    public List<ffprobeReader> jobList = Collections.synchronizedList(new ArrayList());
    public ConcurrentLinkedQueue<ffprobeReader> removeJob = new ConcurrentLinkedQueue();
    
    private ffprobeReader item;

    public JobList() {
        super(JobList.class.getSimpleName());
    }

    @Override
    public void runTick() {
        try {
            Thread.sleep(100);
            
            // remove jobs
            while((item = removeJob.poll()) != null) {
                //jobMap.remove(item.project.projectName);
                jobList.remove(item);
            }
            
            Platform.runLater(printJobs);
            
        } catch (Exception ex) {
            Logger.log(threadName, "error at runTick()", ex);
        }
    }
    
    public Runnable printJobs = new Runnable() {
        ListView listView;
        
        @Override
        public void run() {
            listView = Statics.mainGuiController.getJobListView();
            listView.getItems().clear();
            for(ffprobeReader job : jobList) {
                listView.getItems().add(job.project.projectName + " " + job.project.totalFrames + " frames");
            }
            
            // Resize job view
            if(listView.getItems().size() > 0)
                listView.setPrefHeight(listView.getItems().size() * 24 + 2);
            else
                listView.setPrefHeight(0);
        }
    };
    
}
