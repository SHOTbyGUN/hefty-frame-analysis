/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final HashMap<String, Long> jobMap = new HashMap<>();
    
    private ffprobeReader item;
    private long jobTicks, tempTicks;

    public JobList() {
        super(JobList.class.getSimpleName());
    }

    @Override
    public void runTick() {
        try {
            Thread.sleep(100);
            
            // remove jobs
            while((item = removeJob.poll()) != null) {
                item.project.totalFrames = jobMap.get(item.project.projectName);
                jobMap.remove(item.project.projectName);
            }
            
            // add or update ticks for job
            for(ffprobeReader job : jobList) {
                if(jobMap.containsKey(job.project.projectName))
                    jobMap.put(job.project.projectName, jobMap.get(job.project.projectName) + job.colletTicks());
                else
                    jobMap.put(job.project.projectName, (long) job.colletTicks());
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
            for(Map.Entry<String, Long> pair : jobMap.entrySet()) {
                listView.getItems().add(pair.getKey() + " " + pair.getValue());
            }
            
            // Resize job view
            if(listView.getItems().size() > 0)
                listView.setPrefHeight(listView.getItems().size() * 24 + 2);
            else
                listView.setPrefHeight(0);
        }
    };
    
}
