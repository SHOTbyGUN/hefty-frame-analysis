/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

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
            // maximum of 20 frames per second
            Thread.sleep(50);
            
            // remove jobs
            while((item = removeJob.poll()) != null) {
                //jobMap.remove(item.project.projectName);
                jobList.remove(item);
            }
            
            Platform.runLater(printJobs);
            
        } catch (Exception ex) {
            Logger.log(JobList.class.getSimpleName(), "error at runTick()", ex);
        }
    }
    
    public Runnable printJobs = new Runnable() {
        
        @Override
        public void run() {
            
            try {

                // TODO don't clear + remake... instead do updates on current objects
                VBox vbox = Statics.mainGuiController.getprogressVBox();
                vbox.getChildren().clear();

                StackPane stack;
                ProgressBar progressBar;
                Text text;

                for(ffprobeReader job : jobList) {
                    // Create progress bar and text
                    progressBar = new ProgressBar(calculateProgress(job));
                    progressBar.setMaxWidth(Double.MAX_VALUE);
                    progressBar.setPrefHeight(20);
                    text = new Text(job.project.projectName + " " + job.project.totalFrames + " frames");
                    text.setFont(Font.font(null, 16));

                    // handle containers
                    stack = new StackPane();
                    stack.getChildren().add(progressBar);
                    stack.getChildren().add(text);
                    vbox.getChildren().add(stack);
                    
                    // TODO move this method somewhere else
                    job.project.barGraph.updateSlider();
                }
            
            } catch (Exception ex) {
                Logger.log(JobList.class.getSimpleName(), "Platform.runLater.printJobs error", ex);
            }
        }
    };
    
    
    private double calculateProgress(ffprobeReader job) {
        
        // Return current frame count what we are importing
        // Divide it by expected frames = progress
        return 1.0d * job.project.totalFrames / job.project.expectedFrames;
        // 1.0d * = make sure we are using double to get accurate percentage
    }
    
    @Override
    public void stop() {
        for(ffprobeReader job : jobList) {
            job.stop();
        }
        super.stop();
    }
}
