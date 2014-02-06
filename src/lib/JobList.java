/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package lib;

import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author SHOT(by)GUN
 */
public class JobList {
    
    public ConcurrentLinkedQueue<ffprobeReader> addJob = new ConcurrentLinkedQueue();
    public ConcurrentLinkedQueue<ffprobeReader> removeJob = new ConcurrentLinkedQueue();
    
    // Runtime variables
    private ffprobeReader job, testJob;
    private StackPane stack;
    private ProgressBar progressBar;
    private Text text;
    
    // This runnable is run by UpdateThread... using Platform.runLater()
    public Runnable printJobs = new Runnable() {
        
        @Override
        public void run() {
            
            try {
                
                VBox vbox = Statics.mainGuiController.getprogressVBox();
                
                // remove jobs
                while((job = removeJob.poll()) != null) {
                    
                    for(Node node : vbox.getChildren()) {
                        stack = (StackPane) node;
                        testJob = (ffprobeReader) stack.getUserData();
                        if(job.equals(testJob)) {
                            vbox.getChildren().remove(node);
                            break;
                        }
                            
                    }
                }

                // Update current jobs
                for(Node node : vbox.getChildren()) {
                    stack = (StackPane) node;
                    job = (ffprobeReader) stack.getUserData();
                    
                    if(!job.keepRunning) {
                        vbox.getChildren().remove(node);
                        continue;
                    }
                    
                    // Update progress bar
                    progressBar = (ProgressBar) stack.getChildren().get(0);
                    progressBar.setProgress(calculateProgress(job));
                    
                    // Update text
                    text = (Text) stack.getChildren().get(1);
                    text.setText(job.project.projectName + " " + job.project.totalFrames + " frames");
                }
                
                
                // Add new jobs
                
                while((job = addJob.poll()) != null) {
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
                    
                    // Important add user data
                    stack.setUserData(job);
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
}
