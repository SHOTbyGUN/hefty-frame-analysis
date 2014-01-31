/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package Data;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.control.Tab;
import lib.Logger;
import lib.Statics;
import lib.ffprobeReader;

/**
 *
 * @author SHOT(by)GUN
 */
public final class Project {
    
    // Video file path
    public final String videoFileAbsolutePath;
    public final String projectName;
    
    // data
    public List<Frame> frames;
    public String videoInfoRaw;
    public int expectedFrames;
    public int totalFrames;
    public int durationInSeconds;
    public int frameRate;
    
    // data reader
    private final ffprobeReader reader;
    
    // Graphics
    public final BarGraph barGraph;
    
    // class specific variables
    private final Tab tab;
    
    
    public Project(File videoFile, Tab tab) throws Exception {
        // Process parameters
        this.videoFileAbsolutePath = videoFile.getAbsolutePath();
        this.projectName = videoFile.getName();
        this.tab = tab;
        
        // Create ffprobe data reader
        reader = new ffprobeReader(this);
        
        // Read videoInfo
        readVideoInfo();
        
        // Init variables
        
        // Estimated value ... as 80% extra are audio frames
        expectedFrames = (int) (durationInSeconds * frameRate * 1.8d);
        
        // Create Frame List as estimated size
        // So we don't need to resize the ArrayList while reading frames
        createFrameList(expectedFrames);
        
        // Create graphics
        barGraph = new BarGraph(this);
    }
    
    public Tab getTab() {
        return tab;
    }
    
    public void createFrameList(int size) {
        frames = Collections.synchronizedList(new ArrayList(size));
    }
    
    public void startReadingFrames() {
        // Todo slice read
        reader.start();
    }
    
    private void readVideoInfo() throws Exception {
        BufferedReader input = reader.getVideoInfo();
        if(input == null)
            throw new Exception("Unable to read video info");
        
        videoInfoRaw = "";
        String line;
        String text = "";
        boolean startReading = false;
        
        if(Statics.dumpData) {
            while ((line = input.readLine()) != null) {
                text += line + "\n";
            }

            Logger.log("ReadVideoInfo", text);
        } else {
            while ((line = input.readLine()) != null) {
                if(!startReading) {
                    if(line.startsWith("Input #"))
                        startReading = true;
                    
                } else {
                    videoInfoRaw += line + "\n";
                    
                    // Parse video info here
                    
                    if(line.startsWith("  Duration:")) {
                        durationInSeconds = ParseInfo.getDurationInSeconds(line);
                    }
                    
                    if(line.startsWith("    Stream #")) {
                        int fps = ParseInfo.getFrameRate(line);
                        if(fps > 0)
                            frameRate = fps;
                    }
                }
            }
            
            if(Statics.dumpData) {
                System.out.println("Video Info Raw:");
                System.out.println(videoInfoRaw);
            }
        }
        
        input.close();
    }
    
    public void closeProject() {
        reader.stop();
    }
}
