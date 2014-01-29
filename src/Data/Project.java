/*
 * Software made by SHOT(by)GUN <https://twitter.com/SHOTbyGUN>
 */

package Data;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lib.Logger;
import lib.Statics;
import lib.ffprobeReader;

/**
 *
 * @author SHOT(by)GUN
 */
public class Project {
    
    // Video file path
    public final String videoFileAbsolutePath;
    public final String projectName;
    
    // data
    public List frames;
    public String videoInfo;
    public long totalFrames;
    
    // data reader
    ffprobeReader reader;
    
    // class specific variables
    
    
    
    public Project(String videoFileAbsolutePath) throws Exception {
        this.videoFileAbsolutePath = videoFileAbsolutePath;
        projectName = new File(videoFileAbsolutePath).getName();
        reader = new ffprobeReader(this);
        
        // Init variables
        // TODO frames list should be initialized as about correct size
        frames = new ArrayList();
        
        // After init
        readVideoInfo();
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
        
        videoInfo = "";
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
                    line = line.trim();
                    if(line.startsWith("Input #"))
                        startReading = true;
                    
                } else {
                    videoInfo += line;
                    /*
                    TODO detect duration in seconds * fps * 2
                    = Expected frames total
                    = Progressbar possbile
                    if(line.startsWith("  Duration:")) {
                        line = line.trim();
                        int startPoint = line.indexOf(": ");
                        int endPoint = line.indexOf(", ");
                        System.out.println(line);
                        System.out.println(line.substring(startPoint, endPoint));
                    }
                    */
                }
            }
            
            System.out.println("Video Info:");
            System.out.println(videoInfo);
        }
    }
    
    public void closeProject() {
        reader.stop();
    }
}
